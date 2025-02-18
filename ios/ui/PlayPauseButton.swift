//
//  PlayPauseButton.swift
//  Ruis
//
//  Created by Emil Zeilon on 2024-10-24.
//

import SwiftUI

struct PlayPauseButton: View {
    
    var noiseManager: NoiseMonitor
    var toggleNoise: () -> Void
    
    @ViewBuilder
    var body: some View {
        Spacer()
        Circle()
            .frame(width:130, height: 130, alignment: .center)
            .foregroundColor(Color.black)
            .overlay(
                Image(systemName: noiseManager.playingNoise ? "stop" : "play")
                    .resizable()
                    .frame(width: 70, height: 70)
                    .foregroundColor(.white)
            )
            .onTapGesture {
                toggleNoise()
            }
    }
}
