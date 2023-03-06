actor PropertyManager {
	var globalProperties: [String: String] = [:]

	func setProperty(value: String, forKey: String) {
		globalProperties[forKey] = value
	}

	func removeProperty(forKey: String) {
		globalProperties[forKey] = nil
	}
}
