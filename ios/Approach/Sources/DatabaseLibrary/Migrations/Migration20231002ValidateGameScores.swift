import Foundation
import GRDB
import ScoreKeeperLibrary
import ScoreKeeperModelsLibrary

struct Migration20231002ValidateGameScores: DBMigration {
	static func migrate(_ db: Database) throws {
		let games = try Game
			.all()
			.filter(Column("scoringMethod") == "byFrame")
			.including(
				all: Game
					.hasMany(Frame.self)
					.select(Column("roll0"), Column("roll1"), Column("roll2"))
					.order(Column("index"))
			)
			.fetchAll(db)

		let scoreKeeper = ScoreKeeper()
		for game in games {
			let score = scoreKeeper.calculateScore(from: game.frames.map {
				$0.rolls.map {
					.init(pinsDowned: $0.pinsDowned, didFoul: $0.didFoul)
				}
			})

			try db.execute(sql: "UPDATE game SET score=? WHERE id=?", arguments: [score.gameScore() ?? 0, game.id])
		}
	}
}

extension Migration20231002ValidateGameScores {
	struct Game: Decodable, TableRecord, FetchableRecord {
		static let databaseTableName = "game"
		let id: UUID
		let frames: [Frame]
	}

	struct Frame: Decodable, TableRecord {
		static let databaseTableName = "frame"
		let rolls: [Roll]

		init(from decoder: Decoder) throws {
			let container = try decoder.container(keyedBy: CodingKeys.self)
			let roll0 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll0)
			let roll1 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll1)
			let roll2 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll2)
			self.rolls = [roll0, roll1, roll2].compactMap { $0 }
		}

		enum CodingKeys: CodingKey {
			case gameId
			case roll0
			case roll1
			case roll2
		}
	}

	struct Roll: Decodable {
		let pinsDowned: Set<Pin>
		let didFoul: Bool

		init(pinsDowned: Set<Pin>, didFoul: Bool) {
			self.pinsDowned = pinsDowned
			self.didFoul = didFoul
		}

		init(from decoder: Decoder) throws {
			let container = try decoder.singleValueContainer()
			let bitString = try container.decode(String.self)
			self.init(from: bitString)
		}

		init(from bitString: String) {
			self.didFoul = bitString.first != "0"
			self.pinsDowned = Set(bitString.dropFirst().enumerated().compactMap { index, bit in
				bit == "0" ? nil : Pin(rawValue: index)
			})
		}
	}
}
