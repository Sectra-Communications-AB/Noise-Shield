//
//  SettingsView.swift
//  Ruis
//
//  Created by Emil Zeilon on 2024-11-18.
//

import SwiftUI

struct SettingsView: View {
    @State private var goalVolume: Float =
    mdmManaged() ? minimumNoiseVolume() :
    UserDefaults.standard.value(forKey: MINIMUM_VOLUME_PERCENT_USER_KEY) as? Float ?? 0.55
    var dismiss: () -> Void
    
    var body: some View {
        Color.black.opacity(0.5)
            .ignoresSafeArea()
            .onTapGesture {
                dismiss()
            }
        
        VStack {
            Text(verbatim: "Sectra Noise Shield")
                .font(.headline)
                .padding()
            
            Text("Minimum volume: \(Int(goalVolume * 100))%")
            Slider(value: $goalVolume, in: 0.01...1.0, step: 0.01)
                .onChange(of: goalVolume) { newValue in
                    setMinimumVolumePercentUser(volume: newValue)
                }
                .disabled(mdmManaged())
            
            Text("Version: \(appVersion())")
                .font(.footnote)
                .padding()
            
            Button(String("Close")) {
                dismiss()
            }
            .padding()
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(8)
        }
        .frame(maxWidth: 300)
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(radius: 10)
    }
}

struct SettingsView_Preview: PreviewProvider {
    static var previews: some View {
        ZStack {
            SettingsView(dismiss: {})
        }
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
    }
}
