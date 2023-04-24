public enum Frame {}

extension Frame {
	public struct Roll: Sendable, Equatable, Codable {
		public var pinsDowned: [Pin]
		public var didFoul: Bool

		public init(pinsDowned: [Pin], didFoul: Bool) {
			self.pinsDowned = pinsDowned
			self.didFoul = didFoul
		}

		public init(from bitString: String) {
			assert(bitString.count == 6)
			self.didFoul = bitString.first != "0"
			self.pinsDowned = bitString.dropFirst().enumerated().compactMap { index, bit in
				bit == "0" ? nil : Pin(rawValue: index)
			}
		}

		public init(from decoder: Decoder) throws {
			let container = try decoder.singleValueContainer()
			let bitString = try container.decode(String.self)
			self.init(from: bitString)
		}

		public func encode(to encoder: Encoder) throws {
			let bools = [didFoul] + Pin.fullDeck.map { pinsDowned.contains($0) }
			let bitString = bools.map { $0 ? "1" : "0" }.joined()
			var container = encoder.singleValueContainer()
			try container.encode(bitString)
		}

		public static let `default` = Self(pinsDowned: [], didFoul: false)
	}
}
