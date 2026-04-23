import AVFoundation

@MainActor
final class SpeechEngine: ObservableObject {
    private let synthesizer = AVSpeechSynthesizer()
    private var player: AVAudioPlayer?

    func speak(text: String, language: AppLanguage) {
        stop()
        configureSession(for: .playback)

        let utterance = AVSpeechUtterance(string: text)
        utterance.voice = AVSpeechSynthesisVoice(language: language.rawValue)
        utterance.rate = 0.45
        utterance.pitchMultiplier = 1.1
        utterance.volume = 1.0
        synthesizer.speak(utterance)
    }

    func playAudio(url: URL) {
        stop()
        configureSession(for: .playback)
        do {
            player = try AVAudioPlayer(contentsOf: url)
            player?.play()
        } catch {
            print("SpeechEngine playAudio error: \(error)")
        }
    }

    func stop() {
        synthesizer.stopSpeaking(at: .immediate)
        player?.stop()
        player = nil
    }

    private func configureSession(for category: AVAudioSession.Category) {
        try? AVAudioSession.sharedInstance().setCategory(category, mode: .default)
        try? AVAudioSession.sharedInstance().setActive(true)
    }
}
