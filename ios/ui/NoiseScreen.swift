//
//  ContentView.swift
//  Ruis
//

import SwiftUI
import MediaPlayer

struct NoiseScreen: View {
    
    @State private var inForeground = true
    @ObservedObject var noiseMonitor = NoiseMonitor()
    var volumeSlider: UISlider = UISlider()
    @State private var showAlert = false
    @State private var showSettingsOverlay = false
    @State private var accentColor = Color.white
    
    init() {
        volumeSlider = (MPVolumeView().subviews.filter{NSStringFromClass($0.classForCoder) == "MPVolumeSlider"}.first) as! UISlider
        initSound()
    }
    
    var body: some View {
        NavigationView {
            ZStack {
                switch noiseMonitor.status() {
                case .missing_minimum_volume:
                    noiseYellow.ignoresSafeArea()
                case .idle:
                    noiseBlue.ignoresSafeArea()
                case .noise_ok:
                    noiseGreen.ignoresSafeArea()
                case .loading:
                    Color.white.ignoresSafeArea()
                case .noise_warning:
                    noiseRed.ignoresSafeArea()
                }
                
                VStack (alignment: .center, spacing: 25) {
                    if !appVolumeRestrictionExists() {
                        WarningView(title: String(localized: "NO_VOLUME_INFORMATION_TITLE"), text: "\(String(localized: "NO_VOLUME_INFORMATION_TEXT")) \(deviceModel())", iconName: "info.circle.fill", accentColor: accentColor)
                    } else if noiseMonitor.volumeWarning {
                        WarningView(title: String(localized: "VOLUME_WARNING"), text: String(localized: "VOLUME_WARNING_INFO"), iconName: "exclamationmark.triangle.fill", accentColor: accentColor)
                    }
                    
                    if (noiseMonitor.playingNoise) {
                        if (inForeground) {
                            SoundBarView(accentColor: accentColor)
                                .frame(height: 100)
                                .padding()
                        }
                    } else {
                        Text(String(localized: "PRESS_PLAY_TO_GENERATE_NOISE"))
                            .font(.title3)
                            .fontWeight(.bold)
                            .foregroundColor(accentColor)
                    }
                }
                .padding()
                .position(
                    x: UIScreen.main.bounds.width / 2,
                    y: UIScreen.main.bounds.height * 0.25
                )
                
                PlayPauseButton(noiseManager: noiseMonitor, toggleNoise: toggleNoise)
                    .position(
                        x: UIScreen.main.bounds.width / 2,
                        y: UIScreen.main.bounds.height * 0.75)
                    .disabled(showSettingsOverlay)
            }
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        showSettingsOverlay = true
                    }) {
                        Image(systemName: "ellipsis")
                            .foregroundColor(accentColor)
                            .rotationEffect(.degrees(90))
                    }
                }
            }
            .overlay(content: {
                if showSettingsOverlay {
                    SettingsView {
                        showSettingsOverlay = false
                    }
                }
            })
        }
        .navigationViewStyle(StackNavigationViewStyle())
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.willResignActiveNotification)) { _ in
            inForeground = false
        }
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
            inForeground = true
            updateState()
        }
        .alert(isPresented: $showAlert) {
            Alert(
                title: Text(String(localized: "volume_setting_unavailable_dialog_title")),
                message: Text(verbatim: "\(String(localized: "volume_setting_unavailable_dialog_message")) \(deviceModel())"),
                primaryButton: .default(Text("OK")),
                secondaryButton: .destructive(Text(String(localized: "do_not_remind_me_again")), action: {
                    UserDefaults.standard.set(true, forKey: "DontRemindAgain")
                })
            )
        }
        .onAppear {
            showAlert = !UserDefaults.standard.bool(forKey: "DontRemindAgain") && !appVolumeRestrictionExists()
            accentColor = appVolumeRestrictionExists() ? Color.white : Color.black
            updateState()
        }
    }
    
    func toggleNoise () {
        globalVariables.playNoise.toggle()
        noiseMonitor.playingNoise = globalVariables.playNoise
        
        if globalVariables.playNoise {
            noiseMonitor.previousVolume = volumeSlider.value
            volumeSlider.setValue(minimumNoiseVolume() + 0.02, animated: false)
            playRealNoise()
        } else {
            volumeSlider.setValue(noiseMonitor.previousVolume, animated: false)
            stopRealNoise()
        }
    }
    
    func updateState() {
        if globalVariables.playNoise && !isOnPhoneCall {
            continueRealNoise()
        }
        
        if (inForeground) {
            if ((volumeSlider.value < minimumNoiseVolume() || getVolume() < minimumNoiseVolume()) && globalVariables.playNoise) {
                noiseMonitor.volumeWarning = true
            } else {
                noiseMonitor.volumeWarning = false
            }
            
            Timer.scheduledTimer(withTimeInterval: 1.0, repeats: false) { _ in
                updateState()
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        NoiseScreen()
    }
}
