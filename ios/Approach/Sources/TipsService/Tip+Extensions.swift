import TipsLibrary

extension Tip {
	var preferenceKey: String {
		"tipsService.\(id)"
	}
}
