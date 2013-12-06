//
//  WebViewController.m
//
//
//  Copyright (c) 2013 Ram Kulkarni (ramkulkarni.com). All rights reserved.
//

#import "WebViewController.h"

@interface WebViewController ()

@end

@implementation WebViewController


- (NSString *) getInitialPageName
{
    @throw [NSException exceptionWithName:NSInternalInconsistencyException reason:@"Subclass must implement getInitialPageName function" userInfo:nil];
}

- (id) processFunctionFromJS:(NSString *) name withArgs:(NSArray*) args error:(NSError **) error
{
    @throw [NSException exceptionWithName:NSInternalInconsistencyException reason:@"Subclass must implement processFunctionFromJS function" userInfo:nil];
}


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [webView setDelegate:self];
    
    NSString* initialPage = [self getInitialPageName];
    
    if (initialPage != nil)
    {
        NSURL *url = nil;
        if ([initialPage rangeOfString:@"http://"].length > 0 || [initialPage rangeOfString:@"https://"].length > 0)        {
            url = [NSURL URLWithString:initialPage];
        }
        else
        {
            NSRange range = [initialPage rangeOfString:@"."];
            if (range.length > 0)
            {
                NSString *fileExt = [initialPage substringFromIndex:range.location+1];
                NSString *fileName = [initialPage substringToIndex:range.location];
                
                url = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:fileName ofType:fileExt inDirectory:@"www"]];
            }
        }
        
        if (url != nil)
        {
            
            NSURLRequest *req = [NSURLRequest requestWithURL:url];
            
            [webView loadRequest:req];
            
        }
    }
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    
    NSURL *url = [request URL];
    NSString *urlStr = url.absoluteString;
    
    return [self processURL:urlStr];
    
}


- (BOOL) processURL:(NSString *) url
{
    NSString *urlStr = [NSString stringWithString:url];
    
    NSString *protocolPrefix = @"js2ios://";
    if ([[urlStr lowercaseString] hasPrefix:protocolPrefix])
    {
        urlStr = [urlStr substringFromIndex:protocolPrefix.length];
        
        urlStr = [urlStr stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        
        NSError *jsonError;
        
        NSDictionary *callInfo = [NSJSONSerialization
                                  JSONObjectWithData:[urlStr dataUsingEncoding:NSUTF8StringEncoding]
                                  options:kNilOptions
                                  error:&jsonError];
        
        if (jsonError != nil)
        {
            //call error callback function here
            NSLog(@"Error parsing JSON for the url %@",url);
            return NO;
        }
        
        
        NSString *functionName = [callInfo objectForKey:@"name"];
        if (functionName == nil)
        {
            NSLog(@"Missing function name");
            return NO;
        }
        
        NSArray *argsArray = [callInfo objectForKey:@"args"];
        NSString *callbackKey = [callInfo objectForKey:@"callbackKey"];
        
        
        [self callFunction:functionName withArgs:argsArray callbackKey:callbackKey];
        
        return NO;
        
    }
    
    return YES;
}

- (void) callFunction:(NSString *) name withArgs:(NSArray *) args callbackKey:(NSString *) callbackKey
{
    NSError *error;
    
    id retVal = [self processFunctionFromJS:name withArgs:args error:&error];
    
    if (error != nil) {
        NSString *resultStr = [NSString stringWithString:error.localizedDescription];
        [self callErrorCallback:callbackKey withMessage:resultStr];
    } else {
        [self callSuccessCallback:callbackKey withRetValue:retVal forFunction:name];
    }
    
}

-(void) callErrorCallback:(NSString *) callbackKey withMessage:(NSString *) msg
{
    [webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"iOS.reenter(\"%@\", false, %@);",callbackKey ,msg]];
    
}

-(void) callSuccessCallback:(NSString *) callbackKey withRetValue:(id) retValue forFunction:(NSString *) funcName
{
    NSMutableDictionary *resultDict = [NSMutableDictionary dictionary];
    [resultDict setObject:retValue forKey:@"result"];
    [self callJSFunction:callbackKey withArgs:resultDict];
    
}

-(void) callJSFunction:(NSString *) callbackKey withArgs:(NSMutableDictionary *) args
{
    NSError *jsonError;
    
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:args options:0 error:&jsonError];
    
    if (jsonError != nil)
    {
        //call error callback function here
        NSLog(@"Error creating JSON from the response  : %@",[jsonError localizedDescription]);
        return;
    }
    
    NSString *jsonStr = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];

    if (jsonStr == nil)
    {
        NSLog(@"jsonStr is null. count = %d", [args count]);
    }
    
    [webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"iOS.reenter(\"%@\", true, %@);",callbackKey,jsonStr]];
}

- (void) createError:(NSError**) error withMessage:(NSString *) msg
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:msg forKey:NSLocalizedDescriptionKey];
    
    *error = [NSError errorWithDomain:@"JSiOSBridgeError" code:-1 userInfo:dict];
    
}

-(void) createError:(NSError**) error withCode:(int) code withMessage:(NSString*) msg
{
    NSMutableDictionary *msgDict = [NSMutableDictionary dictionary];
    [msgDict setValue:[NSNumber numberWithInt:code] forKey:@"code"];
    [msgDict setValue:msg forKey:@"message"];
    
    NSError *jsonError;
    
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:msgDict options:0 error:&jsonError];
    
    if (jsonError != nil)
    {
        //call error callback function here
        NSLog(@"Error creating JSON from error message  : %@",[jsonError localizedDescription]);
        return;
    }
    
    NSString *jsonStr = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    
    [self createError:error withMessage:jsonStr];
}

@end
