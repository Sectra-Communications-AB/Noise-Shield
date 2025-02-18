//
//  RuisLib.swift
//  Ruis
//

import Foundation
import AVFoundation
import CallKit

struct globalVariables {
    static var playNoise = false
}

var isOnPhoneCall: Bool {
    return CXCallObserver().calls.contains { $0.hasEnded == false }
}

func initSound () {
    do {
        try AVAudioSession.sharedInstance().setCategory(AVAudioSession.Category.playback, mode: AVAudioSession.Mode.default, options: AVAudioSession.CategoryOptions.mixWithOthers)
        try AVAudioSession.sharedInstance().setActive(true)
    } catch let error {
        print(error.localizedDescription)
    }
    
    NotificationCenter.default.addObserver(forName: AVAudioSession.interruptionNotification, object: nil, queue: nil) { notification in
        if (globalVariables.playNoise) {
            guard let userInfo = notification.userInfo,
                  let interruptionType = AVAudioSession.InterruptionType(rawValue: userInfo[AVAudioSessionInterruptionTypeKey] as? UInt ?? 0) else { return }
            
            switch interruptionType {
            case .began:
                engine.pause()
            case .ended:
                attemptRestartNoise()
            default:
                break
            }
        }
    }
}

func attemptRestartNoise() {
    if (!engine.isRunning) {
        continueRealNoise()
        Timer.scheduledTimer(withTimeInterval: 1.0, repeats: false) { _ in
            attemptRestartNoise()
        }
    }
}

func getVolume() -> Float {
    return AVAudioSession.sharedInstance().outputVolume
}

let sampleRate = Double(44100)
let noiseBuffer = NoiseBuffer(maxBufferSize: Int(sampleRate*2))

func normalize(array: [Float]) -> [Float] {
    // Find the maximum absolute value in the array
    guard let maxAbsValue = array.map({ abs($0) }).max(), maxAbsValue != 0 else {
        return array // Return as is if maxAbsValue is 0 or the array is empty
    }
    
    // Divide each element by the max absolute value
    return array.map { $0 / maxAbsValue }
}

let engine = AVAudioEngine()
let mainMixer = engine.mainMixerNode
let output = engine.outputNode
let outputFormat = output.inputFormat(forBus: 0)

let inputFormat = AVAudioFormat(standardFormatWithSampleRate: sampleRate, channels: 1)

let srcNode = AVAudioSourceNode { _, _, frameCount, audioBufferList -> OSStatus in
    let ablPointer = UnsafeMutableAudioBufferListPointer(audioBufferList)
    let values = noiseBuffer.generateSignal(frameCount: frameCount)
    
    for frame in 0..<Int(frameCount) {
        // Set the same value on all channels (due to the inputFormat we have only 1 channel though).
        for buffer in ablPointer {
            let buf: UnsafeMutableBufferPointer<Float> = UnsafeMutableBufferPointer(buffer)
            buf[frame] = values[frame]
        }
    }
    return noErr
}

func log(_ log: String) {
    let date = Date.now
    let formattedFractional = date.formatted(.dateTime.hour().minute().second().secondFraction(.fractional(3)))
    NSLog("\(formattedFractional) \(log)")
}

func playRealNoise() {
    configureSourceNode()
    do {
        try engine.start()
    } catch {
        print("Could not start engine: \(error)")
    }
}

func configureSourceNode() {
    engine.attach(srcNode)
    engine.connect(srcNode, to: mainMixer, format: inputFormat)
}

func continueRealNoise() {
    if (!engine.isRunning && globalVariables.playNoise) {
        do {
            try engine.start()
        } catch {
            print("Could not start engine: \(error)")
        }
    }
}

func stopRealNoise(){
    engine.stop()
}

let CONFIG_KEY = "com.apple.configuration.managed"
let MINIMUM_VOLUME_KEY = "minimum_volume_percent"
let MINIMUM_VOLUME_KEY_DEVICE_SPECIFIC = "minimum_volume_percent_\(deviceModel())"
let MINIMUM_VOLUME_PERCENT_USER_KEY = "MINIMUM_VOLUME_PERCENT_USER_KEY"

func minimumNoiseVolume () -> Float {
    if let managedConfig = UserDefaults.standard.object(forKey: CONFIG_KEY) as? [String:Any?] {
        if let deviceVolume = managedConfig[MINIMUM_VOLUME_KEY_DEVICE_SPECIFIC] as? Float {
            return deviceVolume
        } else if let managedVolume = managedConfig[MINIMUM_VOLUME_KEY] as? Float {
            return managedVolume
        }
    }
    else if let userVolume = UserDefaults.standard.object(forKey: MINIMUM_VOLUME_PERCENT_USER_KEY) as? Float {
        return userVolume
    }
    return 0.43
}

func deviceModel() -> String {
    var systemInfo = utsname()
    uname(&systemInfo)
    
    let machineMirror = Mirror(reflecting: systemInfo.machine)
    let identifier = machineMirror.children.reduce("") { identifier, element in
        guard let value = element.value as? Int8, value != 0 else {
            return identifier
        }
        return identifier + String(UnicodeScalar(UInt8(value)))
    }
    
    return identifier
}

func mdmManaged() -> Bool {
    guard let managedConfig = UserDefaults.standard.object(forKey: CONFIG_KEY) as? [String:Any?] else {
        return false
    }
    
    return managedConfig[MINIMUM_VOLUME_KEY_DEVICE_SPECIFIC] != nil || managedConfig[MINIMUM_VOLUME_KEY] != nil
}

func setMinimumVolumePercentUser(volume: Float) {
    UserDefaults.standard.set(volume, forKey: MINIMUM_VOLUME_PERCENT_USER_KEY)
}

func appVolumeRestrictionExists () -> Bool {
    return hasMinimumVolumePercentUser() || mdmManaged()
}

func appVersion() -> String {
    return Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "N/A"
}

private func hasMinimumVolumePercentUser() -> Bool {
    return UserDefaults.standard.value(forKey: MINIMUM_VOLUME_PERCENT_USER_KEY) != nil
}

class PinkNoiseGenerator {
    private var b0: Float = 0.0
    private var b1: Float = 0.0
    private var b2: Float = 0.0
    private var b3: Float = 0.0
    private var b4: Float = 0.0
    private var b5: Float = 0.0
    private var b6: Float = 0.0
    
    func generate() -> Float {
        let white = ((Float(arc4random_uniform(UINT32_MAX)) / Float(UINT32_MAX)) * 2 - 1)
        b0 = 0.99886 * b0 + white * 0.0555179
        b1 = 0.99332 * b1 + white * 0.0750759
        b2 = 0.96900 * b2 + white * 0.1538520
        b3 = 0.86650 * b3 + white * 0.3104856
        b4 = 0.55000 * b4 + white * 0.5329522
        b5 = -0.7616 * b5 - white * 0.0168980
        let pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
        b6 = white * 0.115926
        return pink
    }
}

class NoiseBuffer {
    private var primaryBuffer: [Float]
    private var secondaryBuffer: [Float]
    private let maxBufferSize: Int
    private let pinkNoiseGenerator = PinkNoiseGenerator()
    private var bufferPosition = 0
    
    init(maxBufferSize: Int) {
        self.maxBufferSize = maxBufferSize
        primaryBuffer = NoiseBuffer.createNormalizedBuffer(size: maxBufferSize, generator: pinkNoiseGenerator)
        secondaryBuffer = NoiseBuffer.createNormalizedBuffer(size: maxBufferSize, generator: pinkNoiseGenerator)
    }
    
    private func refillBuffer() {
        primaryBuffer = secondaryBuffer
        bufferPosition = 0
        
        DispatchQueue.global().async {
            self.secondaryBuffer = NoiseBuffer.createNormalizedBuffer(size: self.maxBufferSize, generator: self.pinkNoiseGenerator)
        }
    }
    
    func generateSignal(frameCount: UInt32) -> [Float] {
        let frameCountInt = Int(frameCount)
        
        if bufferPosition + frameCountInt > maxBufferSize {
            refillBuffer()
        }
        
        let endPosition = bufferPosition + frameCountInt
        let signal = Array(primaryBuffer[bufferPosition..<endPosition])
        bufferPosition = endPosition
        return signal
    }
    
    private static func createNormalizedBuffer(size: Int, generator: PinkNoiseGenerator) -> [Float] {
        let buffer = (0..<size).map { _ in generator.generate() }
        return normalize(array: buffer)
    }
}
