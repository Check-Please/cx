//
//  chkexViewController.h
//  Checkout Express
//
//  Created by Martin Jelin on 12/3/13.
//  Copyright (c) 2013 Checkout Express. All rights reserved.
//
#import "WebViewController.h"
#import "LocationDelegate.h"
#import <CoreLocation/CoreLocation.h>

@interface chkexViewController : WebViewController
{
    LocationDelegate *locationDelegate;
    IBOutlet UINavigationItem *navItem;
}
@end