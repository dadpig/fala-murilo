import SwiftUI

struct CardItem: Identifiable, Hashable {
    let id: String
    let imageName: String          // asset catalog name
    let labels: [String: String]
    let color: Color

    static let all: [CardItem] = [
        CardItem(id: "eat",      imageName: "comer",    labels: ["en-US": "Eat",      "es-ES": "Comer",    "pt-BR": "Comer"],    color: Color(hex: "FF6B6B")),
        CardItem(id: "drink",    imageName: "beber",    labels: ["en-US": "Drink",    "es-ES": "Beber",    "pt-BR": "Beber"],    color: Color(hex: "4ECDC4")),
        CardItem(id: "play",     imageName: "brincar",  labels: ["en-US": "Play",     "es-ES": "Jugar",    "pt-BR": "Brincar"],  color: Color(hex: "45B7D1")),
        CardItem(id: "bathroom", imageName: "banheiro", labels: ["en-US": "Bathroom", "es-ES": "Baño",     "pt-BR": "Banheiro"], color: Color(hex: "96CEB4")),
        CardItem(id: "dad",      imageName: "pai",      labels: ["en-US": "Dad",      "es-ES": "Papá",     "pt-BR": "Papai"],    color: Color(hex: "FFEAA7")),
        CardItem(id: "mom",      imageName: "mae",      labels: ["en-US": "Mom",      "es-ES": "Mamá",     "pt-BR": "Mamãe"],    color: Color(hex: "DDA0DD")),
        CardItem(id: "yes",      imageName: "sim",      labels: ["en-US": "Yes",      "es-ES": "Sí",       "pt-BR": "Sim"],      color: Color(hex: "98D8C8")),
        CardItem(id: "no",       imageName: "nao",      labels: ["en-US": "No",       "es-ES": "No",       "pt-BR": "Não"],      color: Color(hex: "F7A8A8")),
        CardItem(id: "help",     imageName: "ajuda",    labels: ["en-US": "Help",     "es-ES": "Ayuda",    "pt-BR": "Ajuda"],    color: Color(hex: "FFD700")),
    ]
}

extension Color {
    init(hex: String) {
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let r = Double((int >> 16) & 0xFF) / 255
        let g = Double((int >> 8)  & 0xFF) / 255
        let b = Double( int        & 0xFF) / 255
        self.init(.sRGB, red: r, green: g, blue: b, opacity: 1)
    }
}
