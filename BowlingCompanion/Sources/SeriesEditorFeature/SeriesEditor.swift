import BaseFormFeature
import ComposableArchitecture
import DateTimeLibrary
import Foundation
import PersistenceServiceInterface
import ResourcePickerFeature
import SharedModelsLibrary
import StringsLibrary

extension Series: BaseFormModel {
	static public var modelName = Strings.Series.Model.name
	public var name: String { date.longFormat }
}

extension Alley: PickableResource {
	static public var pickableModelName = Strings.Alleys.Model.name
	public var pickableTitle: String { name }
	public var pickableSubtitle: String? { address }
}

public struct SeriesEditor: ReducerProtocol {
	public typealias Form = BaseForm<Series, Fields>

	public struct Fields: BaseFormState, Equatable {
		public var league: League
		@BindableState public var numberOfGames: Int
		@BindableState public var date = Date()
		public var alleyPicker: ResourcePicker<Alley>.State

		init(league: League, date: Date) {
			self.league = league
			self.numberOfGames = league.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES
			self.date = date
			self.alleyPicker = .init(selected: Set([league.alley].compactMap { $0 }), limit: 1)
		}

		public let isDeleteable = true
		public var isSaveable = true
	}

	public struct State: Equatable {
		public var base: Form.State
		public var initialAlley: Alley?
		public var isAlleyPickerPresented = false
		public let hasAlleysEnabled: Bool

		public init(
			league: League,
			mode: Form.Mode,
			date: Date,
			hasAlleysEnabled: Bool
		) {
			var fields = Fields(league: league, date: date)
			if case let .edit(series) = mode {
				fields.date = series.date
				fields.numberOfGames = series.numberOfGames
			}

			self.base = .init(mode: mode, form: fields)
			self.hasAlleysEnabled = hasAlleysEnabled
		}
	}

	public enum Action: BindableAction, Equatable {
		case loadInitialData
		case leagueAlleyResponse(TaskResult<Alley?>)
		case setAlleyPickerSheet(isPresented: Bool)
		case binding(BindingAction<State>)
		case form(Form.Action)
		case alleyPicker(ResourcePicker<Alley>.Action)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createSeries,
					update: persistenceService.updateSeries,
					delete: persistenceService.deleteSeries
				))
		}

		Scope(state: \.base.form.alleyPicker, action: /Action.alleyPicker) {
			ResourcePicker { try await persistenceService.fetchAlleys(.init(ordering: .byName)) }
		}

		Reduce { state, action in
			switch action {
			case .loadInitialData:
				if let leagueAlley = state.base.form.league.alley {
					return .task {
						await .leagueAlleyResponse(TaskResult {
							let alleys = try await persistenceService.fetchAlleys(.init(filter: .id(leagueAlley), ordering: .byName))
							return alleys.first
						})
					}
				}
				return .none

			case let .leagueAlleyResponse(.success(alley)):
				state.initialAlley = alley
				return .none

			case .leagueAlleyResponse(.failure):
				// TODO: handle error failing to load alley
				return .none

			case let .setAlleyPickerSheet(isPresented):
				state.isAlleyPickerPresented = isPresented
				return .none

			case .alleyPicker(.saveButtonTapped), .alleyPicker(.cancelButtonTapped):
				state.isAlleyPickerPresented = false
				return .none

			case .binding, .form, .alleyPicker:
				return .none
			}
		}
	}
}

extension SeriesEditor.Fields {
	public func model(fromExisting existing: Series?) -> Series {
		@Dependency(\.uuid) var uuid: UUIDGenerator

		return .init(
			league: league.id,
			id: existing?.id ?? uuid(),
			date: date,
			numberOfGames: existing?.numberOfGames ?? league.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES,
			alley: alleyPicker.selected.first
		)
	}
}
