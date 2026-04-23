import SwiftUI

struct PreferencesSheet: View {
    @ObservedObject var store: CardStore
    @ObservedObject var speech: SpeechEngine
    @Environment(\.dismiss) private var dismiss
    @State private var showAbout = false

    var body: some View {
        NavigationStack {
            List {
                // ── Language ───────────────────────────────────────────
                Section {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(AppLanguage.allCases) { lang in
                                Button { store.setLanguage(lang) } label: {
                                    HStack(spacing: 6) {
                                        Text(lang.flag)
                                        Text(lang.label)
                                            .font(.system(size: 14, weight: .semibold, design: .rounded))
                                    }
                                    .padding(.horizontal, 14).padding(.vertical, 9)
                                    .background(
                                        store.language == lang
                                        ? Color(hex: "6C63FF").opacity(0.12)
                                        : Color(.systemGray6)
                                    )
                                    .foregroundStyle(
                                        store.language == lang
                                        ? Color(hex: "6C63FF") : Color.secondary
                                    )
                                    .clipShape(Capsule())
                                    .overlay(
                                        Capsule().stroke(
                                            store.language == lang
                                            ? Color(hex: "6C63FF") : Color.clear,
                                            lineWidth: 1.5
                                        )
                                    )
                                }
                                .buttonStyle(.plain)
                            }
                        }
                        .padding(.vertical, 4)
                    }
                } header: {
                    Text("Idioma / Language")
                }

                // ── Cards ──────────────────────────────────────────────
                Section {
                    ForEach(CardItem.all) { card in
                        CardEditorRow(card: card, store: store, speech: speech)
                    }
                } header: {
                    Text("Cartões / Cards")
                }

                Section {
                    Button { showAbout = true } label: {
                        HStack {
                            Label("Sobre", systemImage: "heart.fill")
                                .foregroundStyle(Color(hex: "FF6B6B"))
                            Spacer()
                            Image(systemName: "chevron.right")
                                .font(.system(size: 12, weight: .semibold))
                                .foregroundStyle(.tertiary)
                        }
                    }
                    .buttonStyle(.plain)
                }
            }
            .navigationTitle("Preferências")
            .navigationBarTitleDisplayMode(.inline)
            .sheet(isPresented: $showAbout) { AboutView() }
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Pronto") { dismiss() }
                        .fontWeight(.semibold)
                }
            }
        }
    }
}
