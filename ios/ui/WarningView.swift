//
//  VolumeWarning.swift
//  Ruis
//
//  Created by Emil Zeilon on 2024-11-06.
//

import SwiftUI

struct WarningView: View {
    
    var title: String
    var text: String
    var iconName: String
    var accentColor: Color
    
    var body: some View {
        HStack(alignment: .top) {
            Image(systemName: iconName)
                .resizable()
                .frame(width: 60, height: 60)
                .foregroundColor(accentColor)
                .frame(alignment: .top)
            VStack(alignment: .leading) {
                Text(title)
                    .font(.title3)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.leading)
                    .foregroundColor(accentColor)
                Text(text)
                    .font(.subheadline)
                .multilineTextAlignment(.leading)
                .foregroundColor(accentColor)
            }
            .padding(.leading, 8)
        }
        .padding()
    }
}

struct VolumeWarning_Preview: PreviewProvider {
    static var previews: some View {
        ZStack {
            WarningView(title: "Warning title", text: "Warning text", iconName: "info.circle.fill", accentColor: .white)
        }
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
        .background(Color.init(hex: "F95041"))
    }
}
