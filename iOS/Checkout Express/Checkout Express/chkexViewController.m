//
//  chkexViewController.m
//  Checkout Express
//
//  Created by Martin Jelin on 12/3/13.
//  Copyright (c) 2013 Checkout Express. All rights reserved.
//


#import "chkexViewController.h"
#import "KeychainItemWrapper.h"

@interface chkexViewController ()

@end

@implementation chkexViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    locationDelegate = [[LocationDelegate alloc] init];
    [locationDelegate.locationManager startUpdatingLocation];
}


- (NSString *) getInitialPageName
{
    return @"app.html";
}

- (NSMutableDictionary *) processFunctionFromJS:(NSString *) name withArgs:(NSArray*) args error:(NSError **) error
{
    NSLog(@"--From JS: %@(%@)", name, [args componentsJoinedByString:@","]);

    //Make sure the correct number of agrs were passed & that the function exists
    int numArgs;
    if([name isEqualToString:@"echo"])
        numArgs = 1;
    else if([name isEqualToString:@"getPos"])
        numArgs = 0;
    else if([name isEqualToString:@"getKeychain"])
        numArgs = 0;
    else if([name isEqualToString:@"setKeychain"])
        numArgs = 1;
    else if([name isEqualToString:@"getTableInfo"])
        numArgs = 0;
    else {
        NSString *resultStr = [NSString stringWithFormat:@"Function '%@' not found", name];
        [self createError:error withCode:-1 withMessage:resultStr];
        return nil;
    }
    
    if(args.count != numArgs) {
        NSString *resultStr = [NSString stringWithFormat:@"Function '%@' expects %d arguments but received %d", name, numArgs, (int) args.count];
        [self createError:error withCode:-1 withMessage:resultStr];
        return nil;
    }
    
    NSMutableDictionary *ret = nil;
    if([name isEqualToString:@"echo"])
        ret = [self jsAPI_echo: [args objectAtIndex:0]];
    else if([name isEqualToString:@"getPos"])
        ret = [self jsAPI_getPos];
    else if([name isEqualToString:@"getKeychain"])
        ret = [self jsAPI_getKeychain];
    else if([name isEqualToString:@"setKeychain"])
        ret = [self jsAPI_setKeychain: [args objectAtIndex:0]];
    else if([name isEqualToString:@"getTableInfo"])
        ret = [self jsAPI_getTableInfo];
    return ret;
}

- (NSMutableDictionary *) jsAPI_echo:(NSString *) text
{
    NSMutableDictionary *ret = [NSMutableDictionary dictionary];
    [ret setObject:text forKey:@"_"];
    return ret;
}

- (NSMutableDictionary *) jsAPI_getPos
{
    NSMutableDictionary *ret = [NSMutableDictionary dictionary];
    [ret setValue:[NSNumber numberWithDouble:locationDelegate.location.coordinate.latitude] forKey:@"latitude"];
    [ret setValue:[NSNumber numberWithDouble:locationDelegate.location.coordinate.longitude] forKey:@"longitude"];
    [ret setValue:[NSNumber numberWithDouble:locationDelegate.location.horizontalAccuracy] forKey:@"accuracy"];
    return ret;
}

- (NSMutableDictionary *) jsAPI_getKeychain
{
    NSMutableDictionary *ret = [NSMutableDictionary dictionary];
    KeychainItemWrapper *kc = [[KeychainItemWrapper alloc] initWithIdentifier:@"id" accessGroup:nil];
    [ret setObject:[kc objectForKey:(__bridge id) kSecValueData] forKey:@"_"];
    return ret;
}

- (NSMutableDictionary *) jsAPI_setKeychain:(NSString *) val
{
    KeychainItemWrapper *kc = [[KeychainItemWrapper alloc] initWithIdentifier:@"id" accessGroup:nil];
    [kc setObject:val forKey:(__bridge id) kSecValueData];
    return nil;
}

- (NSMutableDictionary *) jsAPI_getTableInfo
{
    
    return nil;
}

- (void)viewDidLayoutSubviews {
    webView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
}

@end
