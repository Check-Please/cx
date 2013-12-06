//
//  WebViewController.h
//
//  Copyright (c) 2013 Ram Kulkarni (ramkulkarni.com). All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WebViewInterface.h"

@interface WebViewController : UIViewController <UIWebViewDelegate,WebViewInterface>
{
    IBOutlet UIWebView* webView;
}

- (void) createError:(NSError**) error withCode:(int) code withMessage:(NSString*) msg;

@end
