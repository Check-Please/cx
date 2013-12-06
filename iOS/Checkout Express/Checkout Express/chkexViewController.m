//
//  chkexViewController.m
//  Checkout Express
//
//  Created by Martin Jelin on 12/3/13.
//  Copyright (c) 2013 Checkout Express. All rights reserved.
//


#import "chkexViewController.h"

@interface chkexViewController ()

@end

@implementation chkexViewController

- (NSString *) getInitialPageName
{
    return @"app.html";
}

- (id) processFunctionFromJS:(NSString *) name withArgs:(NSArray*) args error:(NSError **) error
{
    
    if ([name compare:@"sayHello" options:NSCaseInsensitiveSearch] == NSOrderedSame)
    {
        if (args.count > 0)
        {
            return [NSString stringWithFormat:@"Hello %@ !", [args objectAtIndex:0]];
        }
        else
        {
            NSString *resultStr = [NSString stringWithFormat:@"Missing argument in function %@", name];
            [self createError:error withCode:-1 withMessage:resultStr];
            return nil;
        }
    }
    else
    {
        NSString *resultStr = [NSString stringWithFormat:@"Function '%@' not found", name];
        [self createError:error withCode:-1 withMessage:resultStr];
        return nil;
    }
}

- (void)viewDidLayoutSubviews {
    webView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
}

@end
