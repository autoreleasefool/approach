import Foundation

enum FileType: String, CaseIterable {
	case sqlite = "53514C69"
	case zip = "504B0304"

	init?(signature: String) {
		if let type = FileType.allCases.first(where: { $0.rawValue == signature.uppercased() }) {
			self = type
		} else {
			return nil
		}
	}

	static func of(url: URL) throws -> FileType? {
		switch url.pathExtension {
		case "sqlite": return .sqlite
		case "zip": return .zip
		default: break
		}

		let fileHandle = try FileHandle(forReadingFrom: url)
		defer { try? fileHandle.close() }

		guard let bytes = try fileHandle.read(upToCount: 4) else { return nil }
		let signature = bytes.map { String(format: "%02X", $0) }.joined()
		return FileType(signature: signature)
	}
}
