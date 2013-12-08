//
//  LocationDelegate.m
//  Checkout Express
//
//  Created by Martin Jelin on 12/8/13.
//  Copyright (c) 2013 Checkout Express. All rights reserved.
//

#import "LocationDelegate.h"

@implementation LocationDelegate

@synthesize locationManager;
@synthesize location;

- (id) init {
    self = [super init];
    if (self != nil) {
        self.locationManager = [[CLLocationManager alloc] init];
        self.locationManager.delegate = self; // send loc updates to myself
    }
    return self;
}

- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation
{
    location = newLocation;
}

- (void)locationManager:(CLLocationManager *)manager
       didFailWithError:(NSError *)error
{
	NSLog(@"Error: %@", [error description]);
}

@end
