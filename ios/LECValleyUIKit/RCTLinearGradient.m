//
//  BVLinearGradient.m
//  LeDemo
//
//  Created by RaoJia on 2016/12/7.
//  Copyright © 2016年 Facebook. All rights reserved.
//

#import "RCTLinearGradient.h"
#import "RCTConvert.h"
#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>

@implementation RCTLinearGradient

+ (Class)layerClass
{
  return [CAGradientLayer class];
}

- (CAGradientLayer *)gradientLayer
{
  return (CAGradientLayer *)self.layer;
}

- (void)setColors:(NSArray *)colorStrings
{
  NSMutableArray *colors = [NSMutableArray arrayWithCapacity:colorStrings.count];
  for (NSString *colorString in colorStrings) {
    [colors addObject:(id)[RCTConvert UIColor:colorString].CGColor];
  }
  self.gradientLayer.colors = colors;
}

- (void)setStart:(CGPoint)start
{
  self.gradientLayer.startPoint = start;
}

- (void)setEnd:(CGPoint)end
{
  self.gradientLayer.endPoint = end;
}

- (void)setLocations:(NSArray *)locations
{
  self.gradientLayer.locations = locations;
}

@end
