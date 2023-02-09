import AlleysDataProviderInterface
import BaseFormLibrary
import ComposableArchitecture
import DateTimeLibrary
import Foundation
import LanesDataProviderInterface
import PersistenceServiceInterface
import ResourcePickerLibrary
import SharedModelsLibrary
import StringsLibrary

extension Series: BaseFormModel {
	static public var modelName = Strings.Series.title
	public var name: String { date.longFormat }
}

extension Alley: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Alley.title : Strings.Alley.List.title
	}
}

extension Lane: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Lane.title : Strings.Lane.List.title
	}
}

public struct SeriesEditor: ReducerProtocol {
	public typealias Form = BaseForm<Series, Fields>

	public struct Fields: BaseFormState, Equatable {
		public var league: League.ID
		public let hasSetNumberOfGames: Bool
		@BindableState public var numberOfGames: Int
		@BindableState public var date = Date()
		@BindableState public var preBowl: Series.PreBowl = .regularPlay
		@BindableState public var excludeFromStatistics: Series.ExcludeFromStatistics = .include
		public var alleyPicker: ResourcePicker<Alley, Alley.FetchRequest>.State
		public var lanePicker: ResourcePicker<Lane, Lane.FetchRequest>.State

		public let isDeleteable = true
		public var isSaveable = true
		public var saveButtonText: String {
			Strings.Action.start
		}

		init(league: League, date: Date) {
			self.league = league.id
			self.hasSetNumberOfGames = league.numberOfGames != nil
			self.numberOfGames = league.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES
			self.date = date
			self.alleyPicker = .init(
				selected: Set([league.alley].compactMap { $0 }),
				query: .init(filter: nil, ordering: .byName),
				limit: 1,
				showsCancelHeaderButton: false
			)
			let laneQuery: Lane.FetchRequest
			if let alley = league.alley {
				laneQuery = .init(filter: .alley(alley), ordering: .byLabel)
			} else {
				laneQuery = .init(filter: nil, ordering: .byLabel)
			}
			self.lanePicker = .init(
				selected: [],
				query: laneQuery,
				showsCancelHeaderButton: false
			)
		}

		public func hasChanges(from: Self) -> Bool {
			true
		}
	}

	public struct State: Equatable {
		public var base: Form.State
		public var initialAlley: Alley?
		public var initialLanes: IdentifiedArrayOf<Lane>?
		public var isAlleyPickerPresented = false
		public var isLanePickerPresented = false
		public let hasAlleysEnabled: Bool
		public let hasLanesEnabled: Bool

		public init(
			league: League,
			mode: Form.Mode,
			date: Date,
			hasAlleysEnabled: Bool,
			hasLanesEnabled: Bool
		) {
			var fields = Fields(league: league, date: date)
			switch mode {
			case let .edit(series):
				fields.date = series.date
				fields.numberOfGames = series.numberOfGames
				fields.preBowl = series.preBowl
				fields.excludeFromStatistics = series.excludeFromStatistics
			case .create:
				break
			}

			self.base = .init(mode: mode, form: fields)
			self.hasAlleysEnabled = hasAlleysEnabled
			self.hasLanesEnabled = hasLanesEnabled
		}
	}

	public enum Action: BindableAction, Equatable {
		case loadInitialData
		case alleyResponse(TaskResult<Alley?>)
		case lanesResponse(TaskResult<[Lane]>)
		case setAlleyPicker(isPresented: Bool)
		case setLanePicker(isPresented: Bool)
		case binding(BindingAction<State>)
		case form(Form.Action)
		case alleyPicker(ResourcePicker<Alley, Alley.FetchRequest>.Action)
		case lanePicker(ResourcePicker<Lane, Lane.FetchRequest>.Action)
		case didFinishEditing
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.alleysDataProvider) var alleysDataProvider
	@Dependency(\.lanesDataProvider) var lanesDataProvider

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
			ResourcePicker {
				try await alleysDataProvider.fetchAlleys($0)
			}
		}

		Scope(state: \.base.form.lanePicker, action: /Action.lanePicker) {
			ResourcePicker {
				try await lanesDataProvider.fetchLanes($0)
			}
		}

		Reduce { state, action in
			switch action {
			case .loadInitialData:
				return .run { [alley = state.base.form.alleyPicker.selected.first, series = state.base.mode.model] send in
					await withTaskGroup(of: Void.self) { group in
						if let alley {
							group.addTask {
								await send(.alleyResponse(TaskResult {
									let alleys = try await alleysDataProvider.fetchAlleys(.init(filter: .id(alley), ordering: .byName))
									return alleys.first
								}))
							}
						} else {
							group.addTask {
								await send(.alleyResponse(.success(nil)))
							}
						}

						if let series {
							group.addTask {
								await send(.lanesResponse(TaskResult {
									try await lanesDataProvider.fetchLanes(.init(filter: .series(series), ordering: .byLabel))
								}))
							}
						} else {
							group.addTask {
								await send(.lanesResponse(.success([])))
							}
						}
					}
				}

			case let .alleyResponse(.success(alley)):
				state.initialAlley = alley
				return .none

			case .alleyResponse(.failure):
				// TODO: handle error failing to load alley
				return .none

			case let .lanesResponse(.success(lanes)):
				state.initialLanes = .init(uniqueElements: lanes)
				return .none

			case .lanesResponse(.failure):
				// TODO: handle error failing to load lanes
				return .none

			case let .setAlleyPicker(isPresented):
				state.isAlleyPickerPresented = isPresented
				return .none

			case let .setLanePicker(isPresented):
				state.isLanePickerPresented = isPresented
				return .none

			case .alleyPicker(.saveButtonTapped), .alleyPicker(.cancelButtonTapped):
				state.isAlleyPickerPresented = false
				let laneQuery: Lane.FetchRequest
				if let alley = state.base.form.alleyPicker.selected.first {
					laneQuery = .init(filter: .alley(alley), ordering: .byLabel)
				} else {
					laneQuery = .init(filter: nil, ordering: .byLabel)
				}
				state.base.form.lanePicker.query = laneQuery
				return .none

			case .lanePicker(.saveButtonTapped), .lanePicker(.cancelButtonTapped):
				state.isLanePickerPresented = false
				return .none

			case let .form(.delegate(delegateAction)):
				switch delegateAction {
				case let .didSaveModel(series):
					return .task { .form(.callback(.didFinishSaving(.success(series))))}

				case let .didDeleteModel(series):
					return .task { .form(.callback(.didFinishDeleting(.success(series)))) }

				case .didFinishSaving, .didFinishDeleting:
					return .task { .didFinishEditing }
				}

			case .didFinishEditing:
				return .none

			case .binding, .form(.view), .form(.callback), .form(.internal), .alleyPicker, .lanePicker:
				return .none
			}
		}
	}
}

extension SeriesEditor.Fields {
	public func model(fromExisting existing: Series?) -> Series {
		@Dependency(\.uuid) var uuid: UUIDGenerator

		return .init(
			league: league,
			id: existing?.id ?? uuid(),
			date: date,
			numberOfGames: numberOfGames,
			preBowl: preBowl,
			excludeFromStatistics: preBowl == .preBowl ? .exclude : excludeFromStatistics,
			alley: alleyPicker.selected.first
		)
	}
}
