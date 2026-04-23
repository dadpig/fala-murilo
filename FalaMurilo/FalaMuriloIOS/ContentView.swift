import SwiftUI

struct ContentView: View {
    @StateObject private var store  = CardStore()
    @StateObject private var speech = SpeechEngine()
    @State private var showPreferences = false

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 14), count: 3)

    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            ScrollView {
                LazyVGrid(columns: columns, spacing: 14) {
                    ForEach(Array(CardItem.all.enumerated()), id: \.element.id) { index, card in
                        CardView(card: card, index: index, store: store) {
                            handleTap(card: card)
                        }
                    }
                }
                .padding(16)
                .padding(.bottom, 88)
            }
            .background(Color(hex: "FDF8F0"))
            .scrollIndicators(.hidden)

            // Gear button
            Button { showPreferences = true } label: {
                Image(systemName: "gearshape.fill")
                    .font(.system(size: 20, weight: .medium))
                    .foregroundStyle(.secondary)
                    .frame(width: 54, height: 54)
                    .background(.regularMaterial)
                    .clipShape(Circle())
                    .shadow(color: .black.opacity(0.14), radius: 10, x: 0, y: 2)
            }
            .padding(.trailing, 20)
            .padding(.bottom, 28)
        }
        .ignoresSafeArea(edges: .bottom)
        .sheet(isPresented: $showPreferences) {
            PreferencesSheet(store: store, speech: speech)
        }
    }

    private func handleTap(card: CardItem) {
        if store.hasAudio(for: card.id) {
            speech.playAudio(url: store.audioURL(for: card.id))
        } else {
            speech.speak(text: store.displayLabel(for: card), language: store.language)
        }
    }
}
