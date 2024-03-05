import AlleysRepositoryInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import LocationsRepositoryInterface
import ModelsLibrary
import RecentlyUsedServiceInterface
import RepositoryLibrary
import StatisticsModelsLibrary

extension AlleysRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(DatabaseService.self) var database
		@Dependency(RecentlyUsedService.self) var recentlyUsed
		@Dependency(LocationsRepository.self) var locations

		return Self(
			list: { material, pinFall, mechanism, pinBase, ordering in
				let alleys = database.reader().observe {
					let series = Alley.Database.trackableSeries(filter: nil)
					let games = Alley.Database.trackableGames(through: series, filter: nil)
					let averageScore = games
						.average(Game.Database.Columns.score)
						.forKey("average")

					return try Alley.Database
						.all()
						.orderByName()
						.filter(material, pinFall, mechanism, pinBase)
						.including(optional: Alley.Database.location)
						.annotated(with: averageScore)
						.asRequest(of: Alley.List.self)
						.fetchAll($0)
				}

				switch ordering {
				case .byName:
					return alleys
				case .byRecentlyUsed:
					return sort(alleys, byIds: recentlyUsed.observeRecentlyUsedIds(.alleys))
				}
			},
			mostRecentlyUsed: { limit in
				let alleys = database.reader().observe {
					try Alley.Database
						.all()
						.orderByName()
						.including(optional: Alley.Database.location)
						.asRequest(of: Alley.Summary.self)
						.fetchAll($0)
				}

				return prefix(sort(alleys, byIds: recentlyUsed.observeRecentlyUsedIds(.alleys)), ofSize: limit)
			},
			pickable: {
				let alleys = database.reader().observe {
					try Alley.Database
						.all()
						.orderByName()
						.including(optional: Alley.Database.location)
						.asRequest(of: Alley.Summary.self)
						.fetchAll($0)
				}

				return sort(alleys, byIds: recentlyUsed.observeRecentlyUsedIds(.alleys))
			},
			load: { id in
				database.reader().observeOne {
					try Alley.Database
						.filter(Alley.Database.Columns.id == id)
						.including(optional: Alley.Database.location)
						.asRequest(of: Alley.Summary.self)
						.fetchOne($0)
				}
			},
			edit: { id in
				let lanesAlias = TableAlias(name: "lanes")
				return try await database.reader().read {
					try Alley.Database
						.filter(Alley.Database.Columns.id == id)
						.including(optional: Alley.Database.location)
						.including(
							all: Alley.Database.lanes
								.orderByLabel()
								.aliased(lanesAlias)
						)
						.asRequest(of: Alley.EditWithLanes.self)
						.fetchOneGuaranteed($0)
				}
			},
			create: { alley in
				if let location = alley.location {
					try await locations.insertOrUpdate(location)
				}
				try await database.writer().write {
					try alley.insert($0)
				}
			},
			update: { alley in
				if let location = alley.location {
					try await locations.insertOrUpdate(location)
				}
				try await database.writer().write {
					try alley.update($0)
				}
			},
			delete: { id in
				_ = try await database.writer().write {
					try Alley.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
