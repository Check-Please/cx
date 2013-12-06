//
//  WebViewInterface.h
//
//  Copyright (c) 2013 Ram Kulkarni (ramkulkarni.com). All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol WebViewInterface <NSObject>
- (NSString *) getInitialPageName;
- (id) processFunctionFromJS:(NSString *) name withArgs:(NSArray*) args error:(NSError **) error;
@end
