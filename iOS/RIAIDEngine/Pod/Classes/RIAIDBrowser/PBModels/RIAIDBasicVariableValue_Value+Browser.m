//
//  RIAIDBasicVariableValue_Value+Browser.m
//  KCADBrowser
//
//  Created by liweipeng on 2022/4/19.
//

#import "RIAIDBasicVariableValue_Value+Browser.h"

@implementation RIAIDBasicVariableValue_Value (Browser)

- (NSString*)riaidStringValue {
    NSString *result = nil;
    switch (self.type) {
        case RIAIDBasicVariableValue_Type_Bool: {
            result = @(self.b).stringValue;
        } break;
        case RIAIDBasicVariableValue_Type_Integer: {
            result = @(self.i).stringValue;
        } break;
        case RIAIDBasicVariableValue_Type_Double: {
            result = @(self.d).stringValue;
        } break;
        case RIAIDBasicVariableValue_Type_String: {
            result = self.s;
        } break;
        default:
            break;
    }
    return result;
}

@end
