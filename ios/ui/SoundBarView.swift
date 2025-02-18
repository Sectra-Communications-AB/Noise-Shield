//
//  SoundBarView.swift
//  Ruis
//
//  Created by Emil Zeilon on 2024-10-31.
//

import SwiftUI

struct SoundBarView: View {
    let numberOfBars = 20
    @State private var timer = Timer.publish(every: 0.1, on: .main, in: .common).autoconnect()
    @State private var barHeights: [CGFloat] = Array(repeating: 0.0, count: 20)
    var accentColor: Color
    
    var body: some View {
        HStack(spacing: 4) {
            ForEach(0..<numberOfBars, id: \.self) { index in
                RoundedRectangle(cornerRadius: 5)
                    .fill(accentColor)
                    .frame(width: 4, height: barHeights[index])
            }
        }
        .onAppear {
            timer = Timer.publish(every: 0.1, on: .main, in: .common).autoconnect()
        }
        .onReceive(timer) { _ in
            withAnimation(.easeInOut(duration: 0.1)) {
                barHeights = barHeights.map { _ in CGFloat.random(in: 10...100) }
            }
        }
        .onDisappear {
            timer.upstream.connect().cancel()
        }
    }
}

struct SoundBarView_Preview: PreviewProvider {
    static var previews: some View {
        ZStack {
            SoundBarView(accentColor: Color.white)
        }
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
        .background(Color.init(hex: "3FA64D"))
    }
}
