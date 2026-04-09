import type { Language } from '../types'
import './LanguageSelector.css'

interface Props {
  language: Language
  onChange: (lang: Language) => void
}

const LANGUAGES: { code: Language; label: string; flag: string }[] = [
  { code: 'en-US', label: 'English', flag: '🇺🇸' },
  { code: 'es-ES', label: 'Español', flag: '🇪🇸' },
  { code: 'pt-BR', label: 'Português', flag: '🇧🇷' },
]

export function LanguageSelector({ language, onChange }: Props) {
  return (
    <div className="language-selector" role="group" aria-label="Select language">
      {LANGUAGES.map((lang) => (
        <button
          key={lang.code}
          className={`lang-btn${language === lang.code ? ' lang-btn--active' : ''}`}
          onClick={() => onChange(lang.code)}
          aria-pressed={language === lang.code}
          aria-label={lang.label}
        >
          <span className="lang-flag" aria-hidden="true">{lang.flag}</span>
          <span className="lang-label">{lang.label}</span>
        </button>
      ))}
    </div>
  )
}
