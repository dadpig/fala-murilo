import SwiftUI
import AVFoundation

@MainActor
final class CardStore: ObservableObject {
    @Published var language: AppLanguage
    @Published private(set) var version: Int = 0

    private let defaults = UserDefaults.standard
    private let fm = FileManager.default
    private var photoCache: [String: UIImage] = [:]

    private var docs: URL {
        fm.urls(for: .documentDirectory, in: .userDomainMask)[0]
    }

    init() {
        let raw = UserDefaults.standard.string(forKey: "language") ?? AppLanguage.detected().rawValue
        language = AppLanguage(rawValue: raw) ?? AppLanguage.detected()
    }

    // MARK: - Language

    func setLanguage(_ lang: AppLanguage) {
        language = lang
        defaults.set(lang.rawValue, forKey: "language")
        bump()
    }

    // MARK: - Labels

    func customLabel(for cardId: String) -> String? {
        defaults.string(forKey: labelKey(cardId))
    }

    func setLabel(_ text: String, for cardId: String) {
        let trimmed = text.trimmingCharacters(in: .whitespaces)
        if trimmed.isEmpty {
            defaults.removeObject(forKey: labelKey(cardId))
        } else {
            defaults.set(trimmed, forKey: labelKey(cardId))
        }
        bump()
    }

    func displayLabel(for card: CardItem) -> String {
        customLabel(for: card.id)
            ?? card.labels[language.rawValue]
            ?? card.labels["en-US"]
            ?? ""
    }

    // MARK: - Photos

    func photoURL(for cardId: String) -> URL {
        docs.appendingPathComponent("photos/\(cardId).jpg")
    }

    func hasPhoto(for cardId: String) -> Bool {
        fm.fileExists(atPath: photoURL(for: cardId).path)
    }

    func loadPhoto(for cardId: String) -> UIImage? {
        if let cached = photoCache[cardId] { return cached }
        guard hasPhoto(for: cardId),
              let img = UIImage(contentsOfFile: photoURL(for: cardId).path)
        else { return nil }
        photoCache[cardId] = img
        return img
    }

    func savePhoto(_ image: UIImage, for cardId: String) {
        let dir = docs.appendingPathComponent("photos")
        try? fm.createDirectory(at: dir, withIntermediateDirectories: true)
        if let data = image.jpegData(compressionQuality: 0.85) {
            try? data.write(to: photoURL(for: cardId))
        }
        photoCache[cardId] = image
        bump()
    }

    func clearPhoto(for cardId: String) {
        try? fm.removeItem(at: photoURL(for: cardId))
        photoCache.removeValue(forKey: cardId)
        bump()
    }

    // MARK: - Voice recordings

    func audioURL(for cardId: String) -> URL {
        docs.appendingPathComponent("audio/\(cardId).m4a")
    }

    func hasAudio(for cardId: String) -> Bool {
        fm.fileExists(atPath: audioURL(for: cardId).path)
    }

    func clearAudio(for cardId: String) {
        try? fm.removeItem(at: audioURL(for: cardId))
        bump()
    }

    // MARK: - Private

    private func labelKey(_ cardId: String) -> String {
        "label:\(cardId):\(language.rawValue)"
    }

    private func bump() {
        version += 1
    }
}
