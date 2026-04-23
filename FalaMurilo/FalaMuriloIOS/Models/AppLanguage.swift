import Foundation

enum AppLanguage: String, CaseIterable, Identifiable {
    case english = "en-US"
    case spanish = "es-ES"
    case portuguese = "pt-BR"

    var id: String { rawValue }

    var label: String {
        switch self {
        case .english:    return "English"
        case .spanish:    return "Español"
        case .portuguese: return "Português"
        }
    }

    var flag: String {
        switch self {
        case .english:    return "🇺🇸"
        case .spanish:    return "🇪🇸"
        case .portuguese: return "🇧🇷"
        }
    }

    static func detected() -> AppLanguage {
        let code = Locale.current.language.languageCode?.identifier ?? "en"
        if code.hasPrefix("pt") { return .portuguese }
        if code.hasPrefix("es") { return .spanish }
        return .english
    }
}
