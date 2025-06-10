import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary
import RecentlyUsedServiceInterface
import RepositoryLibrary
import SortingLibrary
import TeamsRepositoryInterface

extension TeamsRepository: DependencyKey {
	public static var liveValue: Self {
		Self(
			list: { ordering in
				@Dependency(DatabaseService.self) var database
				@Dependency(RecentlyUsedService.self) var recentlyUsed

				let teams = database.reader().observe {
					try Team.Database
						.all()
						.order { $0.name.collating(.localizedCaseInsensitiveCompare) }
						.including(
							all: Team.Database.members
								.forKey("bowlers")
						)
						.asRequest(of: Team.List.self)
						.fetchAll($0)
				}

				switch ordering {
				case .byName:
					return teams
				case .byRecentlyUsed:
					return sort(teams, byIds: recentlyUsed.observeRecentlyUsedIds(.teams))
				}
			}
		)
	}
}
