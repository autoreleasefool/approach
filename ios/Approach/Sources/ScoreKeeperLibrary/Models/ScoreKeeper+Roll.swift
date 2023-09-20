import ScoreKeeperModelsLibrary

extension ScoreKeeper {
	public struct Roll: Sendable, Equatable, Decodable {
		public let pinsDowned: Set<Pin>
		public let didFoul: Bool

		public init(pinsDowned: Set<Pin>, didFoul: Bool) {
			self.pinsDowned = pinsDowned
			self.didFoul = didFoul
		}

		public init(from decoder: Decoder) throws {
			let container = try decoder.singleValueContainer()
			let bitString = try container.decode(String.self)
			self.init(from: bitString)
		}

		public init(from bitString: String) {
			self.didFoul = bitString.first != "0"
			self.pinsDowned = Set(bitString.dropFirst().enumerated().compactMap { index, bit in
				bit == "0" ? nil : Pin(rawValue: index)
			})
		}
	}
}
