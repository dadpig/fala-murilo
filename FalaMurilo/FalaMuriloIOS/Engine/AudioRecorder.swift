import AVFoundation

@MainActor
final class AudioRecorder: NSObject, ObservableObject {
    @Published var isRecording = false
    @Published var hasRecording = false
    @Published var duration: TimeInterval = 0

    private var recorder: AVAudioRecorder?
    private var previewPlayer: AVAudioPlayer?
    private var timer: Timer?
    private var targetURL: URL?

    // MARK: - Record

    func requestAndRecord(to url: URL) {
        AVAudioApplication.requestRecordPermission { [weak self] granted in
            guard granted else { return }
            Task { @MainActor in self?.beginRecording(to: url) }
        }
    }

    private func beginRecording(to url: URL) {
        let dir = url.deletingLastPathComponent()
        try? FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)

        let settings: [String: Any] = [
            AVFormatIDKey: Int(kAudioFormatMPEG4AAC),
            AVSampleRateKey: 44_100,
            AVNumberOfChannelsKey: 1,
            AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue,
        ]

        do {
            try AVAudioSession.sharedInstance().setCategory(.record, mode: .default)
            try AVAudioSession.sharedInstance().setActive(true)
            recorder = try AVAudioRecorder(url: url, settings: settings)
            recorder?.delegate = self
            recorder?.record()

            targetURL = url
            isRecording = true
            duration = 0

            timer = Timer.scheduledTimer(withTimeInterval: 0.05, repeats: true) { [weak self] _ in
                Task { @MainActor in
                    self?.duration = self?.recorder?.currentTime ?? 0
                }
            }
        } catch {
            print("AudioRecorder beginRecording error: \(error)")
        }
    }

    func stopRecording() {
        recorder?.stop()
        endTimer()
        isRecording = false
        try? AVAudioSession.sharedInstance().setActive(false, options: .notifyOthersOnDeactivation)

        if let url = targetURL, FileManager.default.fileExists(atPath: url.path) {
            hasRecording = true
        }
    }

    // MARK: - Preview

    func playPreview(url: URL) {
        previewPlayer?.stop()
        do {
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .default)
            try AVAudioSession.sharedInstance().setActive(true)
            previewPlayer = try AVAudioPlayer(contentsOf: url)
            previewPlayer?.play()
        } catch {
            print("AudioRecorder playPreview error: \(error)")
        }
    }

    // MARK: - Reset

    func reset() {
        recorder?.stop()
        previewPlayer?.stop()
        endTimer()
        isRecording = false
        hasRecording = false
        duration = 0
        targetURL = nil
    }

    private func endTimer() {
        timer?.invalidate()
        timer = nil
    }
}

extension AudioRecorder: AVAudioRecorderDelegate {
    nonisolated func audioRecorderDidFinishRecording(_ recorder: AVAudioRecorder, successfully flag: Bool) {
        Task { @MainActor in isRecording = false }
    }
}
