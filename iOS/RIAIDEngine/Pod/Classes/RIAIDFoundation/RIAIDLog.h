//
//  RIAIDLog.h
//  Pods
//
//  Created by liweipeng on 2021/12/27.
//

#ifndef RIAIDLog_h
#define RIAIDLog_h

#ifdef __OBJC__

#ifdef DEBUG
#define RIAIDLog(fmt,...) NSLog((@"RIAIDLog: " fmt), ##__VA_ARGS__)
#else
#define RIAIDLog(...)
#endif

#endif

#endif /* RIAIDLog_h */
