import ModelsLibrary
import ScoreKeeperLibrary

extension Frame {
	public struct Rolls: Equatable, Decodable {
		public internal(set) var rolls: [ScoreKeeper.Roll]

		public init(rolls: [ScoreKeeper.Roll]) {
			self.rolls = rolls
		}

		public init(from decoder: Decoder) throws {
			let container = try decoder.container(keyedBy: CodingKeys.self)

			let roll0 = try container.decodeIfPresent(ScoreKeeper.Roll.self, forKey: CodingKeys.roll0)
			let roll1 = try container.decodeIfPresent(ScoreKeeper.Roll.self, forKey: CodingKeys.roll1)
			let roll2 = try container.decodeIfPresent(ScoreKeeper.Roll.self, forKey: CodingKeys.roll2)
			self.rolls = [roll0, roll1, roll2].compactMap { $0 }
		}

		enum CodingKeys: CodingKey {
			case roll0
			case roll1
			case roll2
		}
	}
}
