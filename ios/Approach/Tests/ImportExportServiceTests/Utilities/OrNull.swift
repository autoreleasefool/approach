extension Optional where Wrapped == Int {
	var orNull: String {
		if let self {
			String(describing: self)
		} else {
			"NULL"
		}
	}
}

extension Optional where Wrapped == String {
	var orNull: String {
		if let self {
			"'\(String(describing: self))'"
		} else {
			"NULL"
		}
	}
}
