//
//  NoiseMonitor.swift
//  Ruis
//
//  Created by Emil Zeilon on 2024-10-24.
//

import Foundation

enum NoiseStatus {
    case idle
    case noise_ok
    case noise_warning
    case loading
    case missing_minimum_volume
}

class NoiseMonitor: ObservableObject {
    @Published var playingNoise = globalVariables.playNoise
    @Published var volumeWarning = false
    var previousVolume = Float(0)
    
    func status() -> NoiseStatus {
        if (!appVolumeRestrictionExists()) {
            return .missing_minimum_volume
        }
        if (playingNoise) {
            if (volumeWarning) {
                return .noise_warning
            } else {
                return .noise_ok
            }
        } else {
            return .idle
        }
    }
}
