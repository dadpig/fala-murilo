import SwiftUI
import PhotosUI

struct CardEditorRow: View {
    let card: CardItem
    @ObservedObject var store: CardStore
    @ObservedObject var speech: SpeechEngine

    @State private var labelText = ""
    @State private var pickerItem: PhotosPickerItem?
    @State private var showRecorder = false
    @FocusState private var labelFocused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack(spacing: 12) {
                // Mini polaroid preview
                MiniPolaroidView(card: card, store: store, size: 62)

                // Label field
                VStack(alignment: .leading, spacing: 3) {
                    Text("Label")
                        .font(.system(size: 10, weight: .bold, design: .rounded))
                        .foregroundStyle(.secondary)

                    TextField(
                        card.labels[store.language.rawValue] ?? "",
                        text: $labelText
                    )
                    .font(.system(size: 16, weight: .bold, design: .rounded))
                    .focused($labelFocused)
                    .submitLabel(.done)
                    .onSubmit(commitLabel)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }

            // Action strip
            HStack(spacing: 8) {
                PhotosPicker(selection: $pickerItem, matching: .images) {
                    Label("Foto", systemImage: "photo.on.rectangle")
                        .font(.system(size: 12, weight: .semibold, design: .rounded))
                        .padding(.horizontal, 12).padding(.vertical, 7)
                        .background(Color(hex: "6C63FF").opacity(0.1))
                        .foregroundStyle(Color(hex: "6C63FF"))
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                }
                .buttonStyle(.plain)

                if store.hasPhoto(for: card.id) {
                    Button(role: .destructive) {
                        store.clearPhoto(for: card.id)
                    } label: {
                        Image(systemName: "photo.slash")
                            .font(.system(size: 12, weight: .semibold))
                            .padding(.horizontal, 12).padding(.vertical, 7)
                            .background(Color.red.opacity(0.08))
                            .foregroundStyle(.red)
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                    }
                    .buttonStyle(.plain)
                }

                Spacer()

                // Voice button
                Button { showRecorder = true } label: {
                    HStack(spacing: 5) {
                        Image(systemName: store.hasAudio(for: card.id)
                              ? "waveform.circle.fill" : "mic.circle")
                            .font(.system(size: 14))
                        Text(store.hasAudio(for: card.id) ? "Voz" : "Gravar")
                            .font(.system(size: 12, weight: .semibold, design: .rounded))
                    }
                    .padding(.horizontal, 12).padding(.vertical, 7)
                    .background(store.hasAudio(for: card.id)
                                ? Color.green.opacity(0.1) : Color.orange.opacity(0.1))
                    .foregroundStyle(store.hasAudio(for: card.id) ? .green : .orange)
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.vertical, 6)
        .onAppear(perform: syncLabel)
        .onChange(of: store.language) { _, _ in syncLabel() }
        .onChange(of: labelFocused) { _, focused in
            if !focused { commitLabel() }
        }
        .onChange(of: pickerItem) { _, item in
            guard let item else { return }
            Task {
                if let data = try? await item.loadTransferable(type: Data.self),
                   let image = UIImage(data: data) {
                    store.savePhoto(image, for: card.id)
                }
                pickerItem = nil
            }
        }
        .sheet(isPresented: $showRecorder) {
            VoiceRecorderView(card: card, store: store)
        }
    }

    private func syncLabel() {
        labelText = store.customLabel(for: card.id)
            ?? card.labels[store.language.rawValue]
            ?? ""
    }

    private func commitLabel() {
        let trimmed = labelText.trimmingCharacters(in: .whitespaces)
        let defaultVal = card.labels[store.language.rawValue] ?? ""
        store.setLabel(trimmed == defaultVal ? "" : trimmed, for: card.id)
    }
}
