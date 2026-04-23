import SwiftUI

struct VoiceRecorderView: View {
    let card: CardItem
    @ObservedObject var store: CardStore
    @Environment(\.dismiss) private var dismiss
    @StateObject private var recorder = AudioRecorder()
    @State private var showDeleteConfirm = false

    private var outputURL: URL { store.audioURL(for: card.id) }

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // ── Card preview ────────────────────────────────────────
                MiniPolaroidView(card: card, store: store, size: 130)
                    .padding(.top, 36)
                    .padding(.bottom, 32)

                // ── Status ──────────────────────────────────────────────
                statusSection
                    .frame(height: 72)
                    .padding(.horizontal, 32)

                // ── Waveform ────────────────────────────────────────────
                RecordingPulse(active: recorder.isRecording)
                    .frame(height: 80)
                    .padding(.vertical, 20)

                // ── Controls ────────────────────────────────────────────
                HStack(spacing: 28) {
                    playButton
                    recordButton
                    deleteButton
                }
                .padding(.bottom, 48)

                Spacer()
            }
            .frame(maxWidth: .infinity)
            .background(Color(hex: "FDF8F0").ignoresSafeArea())
            .navigationTitle("Voz · \(store.displayLabel(for: card))")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Pronto") { dismiss() }
                        .fontWeight(.semibold)
                }
            }
            .confirmationDialog(
                "Apagar voz de \"\(store.displayLabel(for: card))\"?",
                isPresented: $showDeleteConfirm,
                titleVisibility: .visible
            ) {
                Button("Apagar", role: .destructive) {
                    store.clearAudio(for: card.id)
                    recorder.reset()
                }
            }
        }
        .onDisappear {
            if recorder.isRecording { recorder.stopRecording() }
        }
    }

    // MARK: - Status text

    @ViewBuilder
    private var statusSection: some View {
        VStack(spacing: 6) {
            if recorder.isRecording {
                Text("Gravando…")
                    .font(.system(size: 18, weight: .bold, design: .rounded))
                    .foregroundStyle(.red)
                Text(String(format: "%.1f s", recorder.duration))
                    .font(.system(size: 15, design: .rounded))
                    .foregroundStyle(.secondary)
            } else if recorder.hasRecording || store.hasAudio(for: card.id) {
                Text("Voz gravada ✓")
                    .font(.system(size: 18, weight: .bold, design: .rounded))
                    .foregroundStyle(.green)
                Text("Toque ▶ para ouvir")
                    .font(.system(size: 14, design: .rounded))
                    .foregroundStyle(.secondary)
            } else {
                Text("Toque para gravar")
                    .font(.system(size: 18, weight: .bold, design: .rounded))
                Text("Sua voz vai tocar ao usar este cartão")
                    .font(.system(size: 14, design: .rounded))
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
            }
        }
    }

    // MARK: - Buttons

    @ViewBuilder
    private var playButton: some View {
        if recorder.hasRecording || store.hasAudio(for: card.id) {
            Button {
                recorder.playPreview(url: outputURL)
            } label: {
                Image(systemName: "play.circle.fill")
                    .font(.system(size: 56))
                    .foregroundStyle(Color(hex: "6C63FF"))
            }
        } else {
            Color.clear.frame(width: 56, height: 56)
        }
    }

    private var recordButton: some View {
        Button(action: toggleRecording) {
            ZStack {
                Circle()
                    .fill(recorder.isRecording ? Color.red : Color(hex: "FF6B6B"))
                    .frame(width: 76, height: 76)
                Image(systemName: recorder.isRecording ? "stop.fill" : "mic.fill")
                    .font(.system(size: 28, weight: .semibold))
                    .foregroundStyle(.white)
            }
        }
        .shadow(color: Color(hex: "FF6B6B").opacity(0.4), radius: 12, x: 0, y: 4)
    }

    @ViewBuilder
    private var deleteButton: some View {
        if store.hasAudio(for: card.id) {
            Button { showDeleteConfirm = true } label: {
                Image(systemName: "trash.circle.fill")
                    .font(.system(size: 56))
                    .foregroundStyle(.red.opacity(0.65))
            }
        } else {
            Color.clear.frame(width: 56, height: 56)
        }
    }

    private func toggleRecording() {
        if recorder.isRecording {
            recorder.stopRecording()
        } else {
            recorder.requestAndRecord(to: outputURL)
        }
    }
}

// MARK: - Pulsing recording indicator

struct RecordingPulse: View {
    let active: Bool
    @State private var animating = false

    var body: some View {
        ZStack {
            if active {
                ForEach(0..<3, id: \.self) { i in
                    Circle()
                        .stroke(Color.red.opacity(0.3 - Double(i) * 0.08), lineWidth: 2)
                        .frame(width: 40 + CGFloat(i) * 28, height: 40 + CGFloat(i) * 28)
                        .scaleEffect(animating ? 1.3 : 1.0)
                        .opacity(animating ? 0 : 1)
                        .animation(
                            .easeOut(duration: 1.4)
                            .repeatForever(autoreverses: false)
                            .delay(Double(i) * 0.3),
                            value: animating
                        )
                }
                Circle()
                    .fill(Color.red)
                    .frame(width: 14, height: 14)
            }
        }
        .onAppear { animating = active }
        .onChange(of: active) { _, v in animating = v }
    }
}

// MARK: - Mini polaroid (shared)

struct MiniPolaroidView: View {
    let card: CardItem
    @ObservedObject var store: CardStore
    var size: CGFloat = 72

    var body: some View {
        VStack(spacing: 0) {
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
            .frame(width: size, height: size)
            .clipped()

            Text(store.displayLabel(for: card))
                .font(.system(size: size * 0.1, weight: .heavy, design: .rounded))
                .foregroundStyle(.black)
                .lineLimit(1)
                .minimumScaleFactor(0.5)
                .padding(.horizontal, size * 0.05)
                .padding(.vertical, size * 0.08)
                .frame(width: size)
                .background(Color.white)
        }
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 2))
        .padding(size * 0.06)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 2))
        .shadow(color: .black.opacity(0.15), radius: 8, x: 0, y: 3)
        .rotationEffect(.degrees(-1.5))
    }
}
