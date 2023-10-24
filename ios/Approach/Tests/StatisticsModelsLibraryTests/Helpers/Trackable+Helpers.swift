import DatabaseModelsLibrary
import Dependencies
import Foundation
import ModelsLibrary

func generateBaseLeagues() -> [League.Database] {
	[
		League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include),
		League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude),
		League.Database.mock(id: UUID(2), name: "Ursa", isArchived: true),
	]
}

func generateSeries(forLeagues: [League.Database]) -> [Series.Database] {
	var series: [Series.Database] = []
	for league in forLeagues {
		series.append(contentsOf: [
			Series.Database.mock(leagueId: league.id, id: UUID(series.count), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include),
			Series.Database.mock(leagueId: league.id, id: UUID(series.count + 1), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude),
			Series.Database.mock(leagueId: league.id, id: UUID(series.count + 2), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include, isArchived: true),
		])
	}
	return series
}

func generateGames(forSeries: [Series.Database]) -> [Game.Database] {
	var games: [Game.Database] = []
	for series in forSeries {
		games.append(contentsOf: [
			Game.Database.mock(seriesId: series.id, id: UUID(games.count), index: games.count, score: 123, excludeFromStatistics: .include),
			Game.Database.mock(seriesId: series.id, id: UUID(games.count + 1), index: games.count + 1, score: 123, excludeFromStatistics: .exclude),
			Game.Database.mock(seriesId: series.id, id: UUID(games.count + 2), index: games.count + 2, score: 123, excludeFromStatistics: .include, isArchived: true),
		])
	}
	return games
}

func generateFrames(forGames: [Game.Database]) -> [Frame.Database] {
	var frames: [Frame.Database]  = []
	for game in forGames {
		frames.append(.mock(gameId: game.id, index: 0))
	}
	return frames
}
