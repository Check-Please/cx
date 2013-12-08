//
//  LocationDelegate.h
//  Checkout Express
//
//  Created by Martin Jelin on 12/8/13.
//  Copyright (c) 2013 Checkout Express. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>

@interface LocationDelegate : NSObject <CLLocationManagerDelegate> {
	CLLocationManager *locationManager;
    CLLocation *location;
}

@property (nonatomic, retain) CLLocationManager *locationManager;
@property (nonatomic, retain) CLLocation *location;

- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation;

- (void)locationManager:(CLLocationManager *)manager
       didFailWithError:(NSError *)error;

@end
