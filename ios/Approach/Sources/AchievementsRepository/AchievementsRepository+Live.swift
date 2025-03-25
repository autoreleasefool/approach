import AchievementsLibrary
import AchievementsRepositoryInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import FeatureFlagsLibrary
import GRDB
import ModelsLibrary

extension AchievementsRepository: DependencyKey {
	public static var liveValue: Self {
		AchievementsRepository(
			list: {
				@Dependency(\.featureFlags) var featureFlags
				guard featureFlags.isFlagEnabled(.achievements) else { return .finished() }

				@Dependency(DatabaseService.self) var database

				let achievementsIndexedOrder = Dictionary(
					uniqueKeysWithValues: EarnableAchievements.allCases.enumerated().map { index, achievement in
						(achievement.title, index)
					}
				)

				return database
					.reader()
					.observe {
						try Achievement.Database
							.all()
							.annotated(
								with: [
									min(Achievement.Database.Columns.earnedAt).forKey("firstEarnedAt"),
									count(Achievement.Database.Columns.id).forKey("count"),
								]
							)
							.group(Achievement.Database.Columns.title)
							.asRequest(of: Achievement.List.self)
							.fetchAll($0)
					}
					.map {
						$0.sorted {
							achievementsIndexedOrder[$0.title] ?? Int.max < achievementsIndexedOrder[$1.title] ?? Int.max
						}
					}
					.eraseToThrowingStream()
			},
			observeNewAchievements: {
				@Dependency(\.featureFlags) var featureFlags
				guard featureFlags.isFlagEnabled(.achievements) else { return .finished }

				@Dependency(DatabaseService.self) var database
				@Dependency(\.date) var date
				let startDate = date.now

				return database.reader()
					.observe {
						try Achievement.Database
							.all()
							.order(Achievement.Database.Columns.earnedAt.desc)
							.filter(Achievement.Database.Columns.earnedAt > startDate)
							.limit(1)
							.asRequest(of: Achievement.Summary.self)
							.fetchAll($0)
					}
					.compactMap { $0.first }
					.eraseToStream()
			}
		)
	}
}
