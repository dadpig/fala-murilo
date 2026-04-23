import SwiftUI

struct CardView: View {
    let card: CardItem
    let index: Int
    @ObservedObject var store: CardStore
    let onTap: () -> Void

    @State private var isPressed = false

    private var tilt: Double { index % 2 == 0 ? -1.4 : 1.4 }

    var body: some View {
        Button(action: onTap) {
            VStack(spacing: 0) {
                // ── Photo area ──────────────────────────────────────────
                ZStack {
                    card.color
                    if let photo = store.loadPhoto(for: card.id) {
                        Image(uiImage: photo)
                            .resizable()
                            .scaledToFill()
                    } else {
                        Image(card.imageName)
                            .resizable()
                            .scaledToFill()
                    }
                }
                .aspectRatio(1, contentMode: .fit)
                .clipped()

                // ── Polaroid strip ──────────────────────────────────────
                VStack(spacing: 4) {
                    Text(store.displayLabel(for: card))
                        .font(.system(size: 14, weight: .heavy, design: .rounded))
                        .foregroundStyle(.black)
                        .multilineTextAlignment(.center)
                        .lineLimit(2)
                        .minimumScaleFactor(0.7)
                        .padding(.horizontal, 4)
                        .padding(.top, 8)

                    // Voice indicator pill
                    if store.hasAudio(for: card.id) {
                        Image(systemName: "waveform")
                            .font(.system(size: 9, weight: .semibold))
                            .foregroundStyle(Color(hex: "6C63FF"))
                            .padding(.bottom, 2)
                    } else {
                        Spacer().frame(height: 2)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.bottom, 10)
                .background(Color.white)
            }
        }
        .buttonStyle(.plain)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 3))
        .shadow(color: .black.opacity(0.12), radius: 8,  x: 0, y: 3)
        .shadow(color: .black.opacity(0.06), radius: 24, x: 0, y: 10)
        .rotationEffect(.degrees(isPressed ? 0 : tilt))
        .scaleEffect(isPressed ? 0.91 : 1.0)
        .simultaneousGesture(
            DragGesture(minimumDistance: 0)
                .onChanged { _ in
                    withAnimation(.easeOut(duration: 0.08)) { isPressed = true }
                }
                .onEnded { _ in
                    withAnimation(.spring(response: 0.35, dampingFraction: 0.55)) {
                        isPressed = false
                    }
                }
        )
    }
}
