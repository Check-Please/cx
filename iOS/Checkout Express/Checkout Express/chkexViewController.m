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

- (void)viewDidLoad
{
    [super viewDidLoad];
    NSURL *url = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:@"app" ofType:@"html" inDirectory:@"www"]];
    
    NSURLRequest *req = [NSURLRequest requestWithURL:url];
    
    [webView loadRequest:req];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
