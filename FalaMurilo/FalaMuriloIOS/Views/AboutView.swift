import SwiftUI

struct AboutView: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                Spacer()

                VStack(spacing: 24) {
                    // Polaroid heart card
                    VStack(spacing: 0) {
                        ZStack {
                            Color(hex: "FF6B6B").opacity(0.18)
                            Text("❤️")
                                .font(.system(size: 72))
                        }
                        .frame(width: 160, height: 160)

                        Text("Fala, Murilo!")
                            .font(.system(size: 15, weight: .heavy, design: .rounded))
                            .foregroundStyle(.black)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 12)
                            .frame(width: 160)
                            .background(Color.white)
                    }
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 3))
                    .padding(10)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 3))
                    .shadow(color: .black.opacity(0.13), radius: 12, x: 0, y: 4)
                    .rotationEffect(.degrees(-2))

                    // Message
                    VStack(spacing: 10) {
                        Text("Feito com amor")
                            .font(.system(size: 22, weight: .heavy, design: .rounded))
                            .foregroundStyle(.primary)

                        Text("para o meu filho")
                            .font(.system(size: 18, weight: .medium, design: .rounded))
                            .foregroundStyle(.secondary)

                        Text("Murilo")
                            .font(.system(size: 32, weight: .black, design: .rounded))
                            .foregroundStyle(Color(hex: "6C63FF"))
                    }
                    .multilineTextAlignment(.center)
                }

                Spacer()
                Spacer()
            }
            .frame(maxWidth: .infinity)
            .background(Color(hex: "FDF8F0").ignoresSafeArea())
            .navigationTitle("Sobre")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Pronto") { dismiss() }
                        .fontWeight(.semibold)
                }
            }
        }
    }
}
