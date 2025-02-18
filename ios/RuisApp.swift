//
//  RuisApp.swift
//  Ruis
//


import SwiftUI
import MediaPlayer

@main
struct RuisApp: App {
    @State private var showSplash = true
    var body: some Scene {
        WindowGroup {
            if (showSplash) {
                SplashScreen()
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                            withAnimation {
                                showSplash = false
                            }
                        }
                    }
            } else {
                NoiseScreen()
            }
        }
    }
}
