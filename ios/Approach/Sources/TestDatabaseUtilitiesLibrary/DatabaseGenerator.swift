import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import ModelsLibrary

public func generatePopulatedDatabase(db: (any DatabaseWriter)? = nil) throws -> any DatabaseWriter {
	let avatars: [Avatar.Database] = [
		.init(id: UUID(0), value: .text("Ye", .rgb(.init(255 / 255.0, 195 / 255.0, 0)))),
		.init(id: UUID(1), value: .text("Bl", .rgb(.init(0, 80 / 255.0, 157 / 255.0)))),
		.init(id: UUID(2), value: .text("Gr", .rgb(.init(0, 127 / 255.0, 95 / 255.0)))),
		.init(id: UUID(3), value: .text("Re", .rgb(.init(191 / 255.0, 33 / 255.0, 30 / 255.0)))),
	]
	let locations: [Location.Database] = [
		.init(id: UUID(0), title: "Skyview", subtitle: "123 Fake Street", latitude: 1.0, longitude: 1.0),
		.init(id: UUID(1), title: "Grandview", subtitle: "456 Real Street", latitude: 2.0, longitude: 2.0),
	]
	let alleys: [Alley.Database] = [
		.init(id: UUID(0), name: "Skyview", material: .wood, pinFall: .strings, mechanism: .dedicated, pinBase: .white, locationId: UUID(0)),
		.init(id: UUID(1), name: "Grandview", material: .synthetic, pinFall: .strings, mechanism: .interchangeable, pinBase: .black, locationId: UUID(1)),
	]
	let lanes: [Lane.Database] = [
		.init(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall),
		.init(alleyId: UUID(0), id: UUID(1), label: "2", position: .noWall),
		.init(alleyId: UUID(0), id: UUID(2), label: "3", position: .noWall),
		.init(alleyId: UUID(0), id: UUID(3), label: "4", position: .noWall),
		.init(alleyId: UUID(0), id: UUID(4), label: "5", position: .noWall),
		.init(alleyId: UUID(0), id: UUID(5), label: "6", position: .noWall),
		.init(alleyId: UUID(0), id: UUID(6), label: "7", position: .noWall),
		.init(alleyId: UUID(0), id: UUID(7), label: "8", position: .noWall),
		.init(alleyId: UUID(0), id: UUID(8), label: "9", position: .noWall),
		.init(alleyId: UUID(0), id: UUID(9), label: "10", position: .noWall),
		.init(alleyId: UUID(0), id: UUID(10), label: "11", position: .noWall),
		.init(alleyId: UUID(0), id: UUID(11), label: "12", position: .rightWall),
		.init(alleyId: UUID(1), id: UUID(12), label: "1", position: .leftWall),
		.init(alleyId: UUID(1), id: UUID(13), label: "2", position: .noWall),
		.init(alleyId: UUID(1), id: UUID(14), label: "3", position: .noWall),
		.init(alleyId: UUID(1), id: UUID(15), label: "4", position: .noWall),
		.init(alleyId: UUID(1), id: UUID(16), label: "5", position: .noWall),
		.init(alleyId: UUID(1), id: UUID(17), label: "6", position: .noWall),
		.init(alleyId: UUID(1), id: UUID(18), label: "7", position: .noWall),
		.init(alleyId: UUID(1), id: UUID(19), label: "8", position: .noWall),
		.init(alleyId: UUID(1), id: UUID(20), label: "9", position: .noWall),
		.init(alleyId: UUID(1), id: UUID(21), label: "10", position: .noWall),
		.init(alleyId: UUID(1), id: UUID(22), label: "11", position: .noWall),
		.init(alleyId: UUID(1), id: UUID(23), label: "12", position: .rightWall),
	]
	let bowlers: [Bowler.Database] = [
		.init(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil),
		.init(id: UUID(1), name: "Sarah", kind: .playable, archivedOn: nil),
		.init(id: UUID(2), name: "Audriana", kind: .opponent, archivedOn: nil),
		.init(id: UUID(3), name: "Jordan", kind: .opponent, archivedOn: nil),
	]
	let gear: [Gear.Database] = [
		.mock(id: UUID(0), name: "Joseph's Shoes", kind: .shoes, bowlerId: UUID(0)),
		.mock(id: UUID(1), name: "Yellow Epco", kind: .bowlingBall, bowlerId: UUID(0), avatarId: UUID(0)),
		.mock(id: UUID(2), name: "Blue Epco", kind: .bowlingBall, bowlerId: UUID(0), avatarId: UUID(1)),
		.mock(id: UUID(3), name: "Green Paramount", kind: .bowlingBall, bowlerId: UUID(1), avatarId: UUID(2)),
		.mock(id: UUID(4), name: "Red Paramount", kind: .bowlingBall, bowlerId: UUID(1), avatarId: UUID(3)),
	]
	let leagues: [League.Database] = [
		.init(bowlerId: UUID(0), id: UUID(0), name: "Majors, 2022-23", recurrence: .repeating, defaultNumberOfGames: 4, additionalPinfall: nil, additionalGames: nil, excludeFromStatistics: .include, archivedOn: nil),
		.init(bowlerId: UUID(0), id: UUID(1), name: "Beer League, 2022-23", recurrence: .repeating, defaultNumberOfGames: 3, additionalPinfall: 1_000, additionalGames: 4, excludeFromStatistics: .include, archivedOn: nil),
		.init(bowlerId: UUID(0), id: UUID(2), name: "Practice", recurrence: .repeating, defaultNumberOfGames: nil, additionalPinfall: nil, additionalGames: nil, excludeFromStatistics: .exclude, archivedOn: nil),
		.init(bowlerId: UUID(0), id: UUID(3), name: "The Open, 2023", recurrence: .once, defaultNumberOfGames: 20, additionalPinfall: nil, additionalGames: nil, excludeFromStatistics: .include, archivedOn: nil),
		.init(bowlerId: UUID(1), id: UUID(4), name: "Majors, 2023-24", recurrence: .repeating, defaultNumberOfGames: 4, additionalPinfall: nil, additionalGames: nil, excludeFromStatistics: .include, archivedOn: nil),
	]
	let series = [
		generateSeries(startDate: Date(timeIntervalSince1970: 1_662_512_400 /* September 6, 2022 */), numberOfSeries: 32, numberOfGames: 4, firstId: 0, league: UUID(0), alley: UUID(1)),
		generateSeries(startDate: Date(timeIntervalSince1970: 1_694_221_200 /* September 8, 2023 */), numberOfSeries: 32, numberOfGames: 3, firstId: 32, league: UUID(1), alley: UUID(1)),
		generateSeries(startDate: Date(timeIntervalSince1970: 1_694_221_200 /* September 8 ,2023 */), numberOfSeries: 32, numberOfGames: nil, firstId: 64, league: UUID(2), alley: nil),
		generateSeries(startDate: Date(timeIntervalSince1970: 1_685_643_496 /* June 1, 2023 */), numberOfSeries: 1, numberOfGames: 20, firstId: 96, league: UUID(3), alley: UUID(0)),
		generateSeries(startDate: Date(timeIntervalSince1970: 1_662_512_400 /* September 6, 2022 */), numberOfSeries: 32, numberOfGames: 4, firstId: 97, league: UUID(4), alley: UUID(1)),
	].flatMap { $0 }
	let (games, gameLanes) = generateGames(forSeries: series)
	let frames = generateFrames(forGames: games)
	let matchPlays = generateMatchPlay(forGames: games)
	let bowlerPreferredGear = generateBowlerPreferredGear(forBowlers: bowlers, withGear: gear)
	let achievementEvents: [AchievementEvent.Database] = [
		.init(id: UUID(0), title: "AppIconsViewed", isConsumed: true),
		.init(id: UUID(1), title: "AppIconsViewed", isConsumed: true),
		.init(id: UUID(2), title: "AppIconsViewed", isConsumed: false),
		.init(id: UUID(3), title: "TenYearsBadgeClaimed", isConsumed: true),
		.init(id: UUID(4), title: "TenYearsBadgeClaimed", isConsumed: false),
	]
	let achievements: [Achievement.Database] = [
		.init(id: UUID(0), title: "Iconista", earnedAt: Date(timeIntervalSince1970: 1_662_512_400 /* September 6, 2022 */)),
		.init(id: UUID(1), title: "Iconista", earnedAt: Date(timeIntervalSince1970: 1_694_221_200 /* September 8 ,2023 */)),
		.init(id: UUID(2), title: "TenYears", earnedAt: Date(timeIntervalSince1970: 1_685_643_496 /* June 1, 2023 */)),
	]

	return try initializeApproachDatabase(
		withAvatars: .custom(avatars),
		withLocations: .custom(locations),
		withAlleys: .custom(alleys),
		withLanes: .custom(lanes),
		withBowlers: .custom(bowlers),
		withGear: .custom(gear),
		withLeagues: .custom(leagues),
		withSeries: .custom(series.map(\.0)),
		withGames: .custom(games),
		withGameLanes: .custom(gameLanes),
		withGameGear: .zero,
		withFrames: .custom(frames),
		withMatchPlays: .custom(matchPlays),
		withBowlerPreferredGear: .custom(bowlerPreferredGear),
		withAchievementEvents: .custom(achievementEvents),
		withAchievements: .custom(achievements),
		to: db
	)
}

private func generateSeries(
	startDate: Date,
	numberOfSeries: Int,
	numberOfGames: Int?,
	firstId: Int,
	league: League.ID,
	alley: Alley.ID?
) -> [(Series.Database, numberOfGames: Int)] {
	var date = startDate
	var series: [(Series.Database, numberOfGames: Int)] = []
	while series.count < numberOfSeries {
		series.append((
			Series.Database(
				leagueId: league,
				id: UUID(firstId + series.count),
				date: Date(timeIntervalSince1970: date.timeIntervalSince1970),
				appliedDate: nil,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: alley ?? UUID(series.count % 2),
				archivedOn: nil
			),
			numberOfGames: numberOfGames ?? series.count % 4 + 1
		))
		date.addTimeInterval(604_800)
	}
	return series
}

private func generateGames(forSeries: [(Series.Database, numberOfGames: Int)]) -> ([Game.Database], [GameLane.Database]) {
	var games: [Game.Database] = []
	var gameLanes: [GameLane.Database] = []
	func generateGame() -> (Series.ID) -> Game.Database {
		var gameId = -1
		var seriesGames: [Series.ID: Int] = [:]
		let baseGamesScores = [192, 269, 165, 163]

		return { seriesId in
			let lastIndex = seriesGames[seriesId] ?? -1
			seriesGames[seriesId] = lastIndex + 1
			gameId += 1
			return .init(
				seriesId: seriesId,
				id: UUID(gameId),
				index: lastIndex + 1,
				score: baseGamesScores[gameId % baseGamesScores.count],
				locked: .locked,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include,
				duration: 0,
				archivedOn: nil
			)
		}
	}

	let gameGenerator = generateGame()
	for series in forSeries {
		var seriesGames: [Game.Database] = []
		for _ in (0..<series.numberOfGames) {
			seriesGames.append(gameGenerator(series.0.id))
		}
		let laneIds: Range<Int>?
		switch series.0.alleyId {
		case UUID(0): laneIds = (0..<12)
		case UUID(1): laneIds = (12..<24)
		default: laneIds = nil
		}
		gameLanes.append(contentsOf: generateLanes(forGames: seriesGames, ids: laneIds))
		games.append(contentsOf: seriesGames)
	}

	return (games, gameLanes)
}

private func generateLanes(forGames: [Game.Database], ids: Range<Int>?) -> [GameLane.Database] {
	guard let ids else { return [] }
	var lanes: [GameLane.Database] = []
	for (index, game) in forGames.enumerated() {
		lanes.append(.init(gameId: game.id, laneId: UUID(ids[index % ids.count + ids.lowerBound])))
		lanes.append(.init(gameId: game.id, laneId: UUID(ids[(index + 1) % ids.count + ids.lowerBound])))
	}
	return lanes
}

private func generateFrames(forGames: [Game.Database]) -> [Frame.Database] {
	var frames: [Frame.Database] = []
	for game in forGames {
		switch game.score {
		case 192:
			frames.append(contentsOf: [
				.init(gameId: game.id, index: 0, roll0: "000100", roll1: "011000", roll2: "000011", ball0: UUID(1), ball1: UUID(1), ball2: UUID(1)),
				.init(gameId: game.id, index: 1, roll0: "001111", roll1: "000000", roll2: "010000", ball0: UUID(2), ball1: UUID(2), ball2: UUID(2)),
				.init(gameId: game.id, index: 2, roll0: "011110", roll1: "000001", roll2: nil, ball0: UUID(3), ball1: UUID(3), ball2: nil),
				.init(gameId: game.id, index: 3, roll0: "011111", roll1: nil, roll2: nil, ball0: UUID(4), ball1: nil, ball2: nil),
				.init(gameId: game.id, index: 4, roll0: "001111", roll1: "010000", roll2: nil, ball0: UUID(1), ball1: UUID(1), ball2: nil),
				.init(gameId: game.id, index: 5, roll0: "011110", roll1: "000000", roll2: "000000", ball0: UUID(2), ball1: UUID(2), ball2: UUID(2)),
				.init(gameId: game.id, index: 6, roll0: "001110", roll1: "000000", roll2: "000000", ball0: UUID(3), ball1: UUID(3), ball2: UUID(3)),
				.init(gameId: game.id, index: 7, roll0: "011100", roll1: "000011", roll2: nil, ball0: UUID(4), ball1: UUID(4), ball2: nil),
				.init(gameId: game.id, index: 8, roll0: "011000", roll1: "000111", roll2: nil, ball0: UUID(1), ball1: UUID(1), ball2: nil),
				.init(gameId: game.id, index: 9, roll0: "011000", roll1: "000011", roll2: "000000", ball0: UUID(2), ball1: UUID(2), ball2: UUID(2)),
			])
		case 269:
			frames.append(contentsOf: [
				.init(gameId: game.id, index: 0, roll0: "000101", roll1: "011000", roll2: "000010", ball0: UUID(1), ball1: UUID(1), ball2: UUID(1)),
				.init(gameId: game.id, index: 1, roll0: "001100", roll1: "000011", roll2: "010000", ball0: UUID(2), ball1: UUID(2), ball2: UUID(2)),
				.init(gameId: game.id, index: 2, roll0: "011111", roll1: nil, roll2: nil, ball0: UUID(3), ball1: nil, ball2: nil),
				.init(gameId: game.id, index: 3, roll0: "011111", roll1: nil, roll2: nil, ball0: UUID(4), ball1: nil, ball2: nil),
				.init(gameId: game.id, index: 4, roll0: "011111", roll1: nil, roll2: nil, ball0: UUID(1), ball1: nil, ball2: nil),
				.init(gameId: game.id, index: 5, roll0: "000111", roll1: "000000", roll2: "000000", ball0: UUID(2), ball1: UUID(2), ball2: UUID(2)),
				.init(gameId: game.id, index: 6, roll0: "011101", roll1: "000010", roll2: nil, ball0: UUID(3), ball1: UUID(3), ball2: nil),
				.init(gameId: game.id, index: 7, roll0: "011101", roll1: "000000", roll2: "000000", ball0: UUID(4), ball1: UUID(4), ball2: UUID(4)),
				.init(gameId: game.id, index: 8, roll0: "011111", roll1: nil, roll2: nil, ball0: UUID(1), ball1: nil, ball2: nil),
				.init(gameId: game.id, index: 9, roll0: "011111", roll1: "011111", roll2: "000100", ball0: UUID(2), ball1: UUID(2), ball2: UUID(2)),
			])
		case 165:
			frames.append(contentsOf: [
				.init(gameId: game.id, index: 0, roll0: "111111", roll1: nil, roll2: nil, ball0: UUID(1), ball1: nil, ball2: nil),
				.init(gameId: game.id, index: 1, roll0: "111111", roll1: nil, roll2: nil, ball0: UUID(2), ball1: nil, ball2: nil),
				.init(gameId: game.id, index: 2, roll0: "001111", roll1: "000000", roll2: "000000", ball0: UUID(3), ball1: UUID(3), ball2: UUID(3)),
				.init(gameId: game.id, index: 3, roll0: "001111", roll1: "010000", roll2: nil, ball0: UUID(4), ball1: UUID(4), ball2: nil),
				.init(gameId: game.id, index: 4, roll0: "011110", roll1: "000000", roll2: "000000", ball0: UUID(1), ball1: UUID(1), ball2: UUID(1)),
				.init(gameId: game.id, index: 5, roll0: "011110", roll1: "000001", roll2: nil, ball0: UUID(2), ball1: UUID(2), ball2: nil),
				.init(gameId: game.id, index: 6, roll0: "011000", roll1: "000011", roll2: "000000", ball0: UUID(3), ball1: UUID(3), ball2: UUID(3)),
				.init(gameId: game.id, index: 7, roll0: "011000", roll1: "000011", roll2: "000000", ball0: UUID(4), ball1: UUID(4), ball2: UUID(4)),
				.init(gameId: game.id, index: 8, roll0: "000011", roll1: "011000", roll2: "000100", ball0: UUID(1), ball1: UUID(1), ball2: UUID(1)),
				.init(gameId: game.id, index: 9, roll0: "000011", roll1: "000100", roll2: "011000", ball0: UUID(2), ball1: UUID(2), ball2: UUID(2)),
			])
		case 163:
			frames.append(contentsOf: [
				.init(gameId: game.id, index: 0, roll0: "101110", roll1: "010000", roll2: "000001", ball0: UUID(1), ball1: UUID(1), ball2: UUID(1)),
				.init(gameId: game.id, index: 1, roll0: "100110", roll1: "011000", roll2: "000001", ball0: UUID(2), ball1: UUID(2), ball2: UUID(2)),
				.init(gameId: game.id, index: 2, roll0: "110111", roll1: "001000", roll2: nil, ball0: UUID(3), ball1: UUID(3), ball2: nil),
				.init(gameId: game.id, index: 3, roll0: "111101", roll1: "000010", roll2: nil, ball0: UUID(4), ball1: UUID(4), ball2: nil),
				.init(gameId: game.id, index: 4, roll0: "000111", roll1: "000000", roll2: "011000", ball0: UUID(1), ball1: UUID(1), ball2: UUID(1)),
				.init(gameId: game.id, index: 5, roll0: "000111", roll1: "011000", roll2: nil, ball0: UUID(2), ball1: UUID(2), ball2: nil),
				.init(gameId: game.id, index: 6, roll0: "011100", roll1: "000000", roll2: "000011", ball0: UUID(3), ball1: UUID(3), ball2: UUID(3)),
				.init(gameId: game.id, index: 7, roll0: "001100", roll1: "010011", roll2: nil, ball0: UUID(4), ball1: UUID(4), ball2: nil),
				.init(gameId: game.id, index: 8, roll0: "001110", roll1: "010001", roll2: nil, ball0: UUID(1), ball1: UUID(1), ball2: nil),
				.init(gameId: game.id, index: 9, roll0: "011111", roll1: "000110", roll2: "011001", ball0: UUID(2), ball1: UUID(2), ball2: UUID(2)),
			])
		default:
			fatalError("Cannot generate game with score \(game.score)")
		}
	}
	return frames
}

private func generateMatchPlay(forGames: [Game.Database]) -> [MatchPlay.Database] {
	var matchPlays: [MatchPlay.Database] = []
	let scores = [100, 150, 200, 250]
	let results: [MatchPlay.Result] = [.lost, .won, .tied]
	for (index, game) in forGames.enumerated() {
		matchPlays.append(.init(gameId: game.id, id: UUID(index), opponentId: UUID(index % 3), opponentScore: scores[index % scores.count], result: results[index % results.count]))
	}
	return matchPlays
}

private func generateBowlerPreferredGear(forBowlers: [Bowler.Database], withGear: [Gear.Database]) -> [BowlerPreferredGear.Database] {
	forBowlers.flatMap { bowler in
		withGear.map { gear in
			BowlerPreferredGear.Database(bowlerId: bowler.id, gearId: gear.id)
		}
	}
}
