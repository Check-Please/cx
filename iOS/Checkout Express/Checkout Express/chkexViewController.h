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
#import <ESTBeaconManager.h>

@interface chkexViewController : WebViewController
{
    IBOutlet UINavigationItem *navItem;
    LocationDelegate *locationDelegate;
    NSArray *bluetoothIDs;
    NSArray *bluetoothRSSIs;
}
@end