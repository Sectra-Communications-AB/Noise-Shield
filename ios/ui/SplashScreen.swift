//
//  SplashScreen.swift
//  Ruis
//
//  Created by Emil Zeilon on 2024-11-22.
//

import SwiftUI

struct SplashScreen: View {
    var body: some View {
        ZStack {
            Image("SplashImage")
                .resizable()
                .scaledToFill()
                .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
                .edgesIgnoringSafeArea(.all)
        }
    }
}

struct SplashScreen_Previews: PreviewProvider {
    static var previews: some View {
        SplashScreen()
    }
}
