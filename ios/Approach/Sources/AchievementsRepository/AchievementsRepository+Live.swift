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
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					try Achievement.Database
						.all()
						.annotated(
							with: [
								min(Achievement.Database.Columns.earnedAt).forKey("firstEarnedAt"),
								count(Achievement.Database.Columns.id).forKey("count"),
							]
						)
						.group(Achievement.Database.Columns.title)
						.asRequest(of: Achievement.Counted.self)
						.fetchAll($0)
				}
			},
			observeNewAchievements: { .finished }
		)
	}
}
