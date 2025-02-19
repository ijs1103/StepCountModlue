//
//  StepCountModule.swift
//  StepCountModule
//
//  Created by 이주상 on 2/19/25.
//

import Foundation
import CoreMotion

@objc(StepCountModule)

class StepCountModule: NSObject {
  private let pedometer = CMPedometer()
  
  @objc func isStepCountingAvailable(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
    if CMPedometer.isStepCountingAvailable() {
      resolve(true)
    } else {
      resolve(false)
    }
  }
  // 그날 하루의 걸음수 데이터
  @objc func getStepCount(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
    let start = Calendar.current.startOfDay(for: Date())
    let now = Date()

    pedometer.queryPedometerData(from: start, to: now) { data, error in
      if let error = error {
        reject("STEP_COUNT_ERROR", "Failed to fetch step count", error)
      } else if let data = data {
        resolve(["steps": data.numberOfSteps])
      } else {
        reject("STEP_COUNT_ERROR", "No data available", nil)
      }
    }
  }
  
  @objc func startStepUpdates(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
    guard CMPedometer.isStepCountingAvailable() else {
      reject("STEP_COUNT_NOT_AVAILABLE", "Step counting is not available on this device", nil)
      return
    }
    
    pedometer.startUpdates(from: Calendar.current.startOfDay(for: Date())) { data, error in
      if let error = error {
        reject("STEP_COUNT_ERROR", "Failed to fetch step updates", error)
      } else if let data = data {
        resolve(["steps": data.numberOfSteps])
      }
    }
  }
  
  @objc func stopStepUpdates() {
    pedometer.stopUpdates()
  }
  
  @objc static func requiresMainQueueSetup() -> Bool {
    return true
  }
}
