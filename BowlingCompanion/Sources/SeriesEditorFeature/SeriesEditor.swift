import AlleysDataProviderInterface
import BaseFormLibrary
import ComposableArchitecture
import DateTimeLibrary
import FeatureActionLibrary
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
		public let league: League
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
			self.league = league
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

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case setAlleyPicker(isPresented: Bool)
			case setLanePicker(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case didLoadAlley(TaskResult<Alley?>)
			case didLoadLanes(TaskResult<[Lane]>)
			case form(Form.Action)
			case alleyPicker(ResourcePicker<Alley, Alley.FetchRequest>.Action)
			case lanePicker(ResourcePicker<Lane, Lane.FetchRequest>.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.alleysDataProvider) var alleysDataProvider
	@Dependency(\.lanesDataProvider) var lanesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.internal..Action.InternalAction.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createSeries,
					update: persistenceService.updateSeries,
					delete: persistenceService.deleteSeries
				))
		}

		Scope(state: \.base.form.alleyPicker, action: /Action.internal..Action.InternalAction.alleyPicker) {
			ResourcePicker(fetchResources: alleysDataProvider.fetchAlleys)
		}

		Scope(state: \.base.form.lanePicker, action: /Action.internal..Action.InternalAction.lanePicker) {
			ResourcePicker(fetchResources: lanesDataProvider.fetchLanes)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .run { [alley = state.base.form.alleyPicker.selected.first, series = state.base.mode.model] send in
						await withTaskGroup(of: Void.self) { group in
							if let alley {
								group.addTask {
									await send(.internal(.didLoadAlley(TaskResult {
										let alleys = try await alleysDataProvider.fetchAlleys(.init(filter: .id(alley), ordering: .byName))
										return alleys.first
									})))
								}
							} else {
								group.addTask {
									await send(.internal(.didLoadAlley(.success(nil))))
								}
							}

							if let series {
								group.addTask {
									await send(.internal(.didLoadLanes(TaskResult {
										try await lanesDataProvider.fetchLanes(.init(filter: .series(series), ordering: .byLabel))
									})))
								}
							} else {
								group.addTask {
									await send(.internal(.didLoadLanes(.success([]))))
								}
							}
						}
					}

				case let .setAlleyPicker(isPresented):
					state.isAlleyPickerPresented = isPresented
					return .none

				case let .setLanePicker(isPresented):
					state.isLanePickerPresented = isPresented
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadAlley(.success(alley)):
					state.initialAlley = alley
					return .none

				case .didLoadAlley(.failure):
					// TODO: handle error failing to load alley
					return .none

				case let .didLoadLanes(.success(lanes)):
					state.initialLanes = .init(uniqueElements: lanes)
					return .none

				case .didLoadLanes(.failure):
					// TODO: handle error failing to load lanes
					return .none

				case let .alleyPicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.isAlleyPickerPresented = false
						let laneQuery: Lane.FetchRequest
						if let alley = state.base.form.alleyPicker.selected.first {
							laneQuery = .init(filter: .alley(alley), ordering: .byLabel)
						} else {
							laneQuery = .init(filter: nil, ordering: .byLabel)
						}
						state.base.form.lanePicker.query = laneQuery
						return .none
					}

				case let .lanePicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.isLanePickerPresented = false
						return .none
					}

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didSaveModel(series):
						return .task { .internal(.form(.callback(.didFinishSaving(.success(series))))) }

					case let .didDeleteModel(series):
						return .task { .internal(.form(.callback(.didFinishDeleting(.success(series))))) }

					case .didFinishSaving, .didFinishDeleting:
						return .task { .delegate(.didFinishEditing) }
					}

				case .form(.view), .form(.callback), .form(.internal):
					return .none

				case .alleyPicker(.internal), .alleyPicker(.view):
					return .none

				case .lanePicker(.internal), .lanePicker(.view):
					return .none
				}

			case .binding, .delegate:
				return .none
			}
		}
	}
}

extension SeriesEditor.Fields {
	public func model(fromExisting existing: Series?) -> Series {
		@Dependency(\.uuid) var uuid: UUIDGenerator
		let shouldExcludeFromStatistics: Series.ExcludeFromStatistics
		switch league.excludeFromStatistics {
		case .include:
			shouldExcludeFromStatistics = preBowl == .preBowl ? .exclude : self.excludeFromStatistics
		case .exclude:
			shouldExcludeFromStatistics = .exclude
		}

		return .init(
			league: league.id,
			id: existing?.id ?? uuid(),
			date: date,
			numberOfGames: numberOfGames,
			preBowl: preBowl,
			excludeFromStatistics: shouldExcludeFromStatistics,
			alley: alleyPicker.selected.first
		)
	}
}
