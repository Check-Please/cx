//
//  LOLViewController.m
//  SuchMeme
//
//  Created by Isaac Lim on 11/1/13.
//  Copyright (c) 2013 isaacl.net. All rights reserved.
//

#import "LOLViewController.h"

#define kMemeBaseURL @"http://api.automeme.net/"
#define kMemeEndpoint @"text.json"
#define kCatAPI @"http://thecatapi.com/api/images/get?format=src&type=jpg&results_per_page=1"
#define kCatImage @"placeholderCat"

@implementation LOLViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    
}

#pragma mark - Helpers

//- (NSArray *)pickBestMeme:(NSArray *)possibleMemes
//{
//    for (NSString *meme in possibleMemes) {
//        if ([meme rangeOfString:@"_"].length > 0) continue;
//
//        NSMutableArray *halves = [[meme componentsSeparatedByCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@".,"]] mutableCopy];
//        [halves filterUsingPredicate:[NSPredicate predicateWithBlock:^BOOL(NSString *str, NSDictionary *bindings) { return str.length > 0; }]];
//        if (halves.count == 2) {
//            NSLog(@"Halves: %@", halves);
//            return halves;
//        }
//    }
//    return nil;
//}
//
//- (UIImage *)saveToImage
//{
//    UIGraphicsBeginImageContextWithOptions(self.canvas.frame.size, NO, [UIScreen mainScreen].scale);
//    [self.canvas drawViewHierarchyInRect:self.canvas.bounds afterScreenUpdates:YES];
//    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
//    UIGraphicsEndImageContext();
//    return image;
//}

@end
