import AlleysDataProviderInterface
import BaseFormFeature
import ComposableArchitecture
import PersistenceServiceInterface
import ResourcePickerFeature
import SharedModelsLibrary
import StringsLibrary

extension League: BaseFormModel {
	static public var modelName = Strings.League.title
}

extension Alley: PickableResource {
	static public var pickableModelName = Strings.Alley.title
}

public struct LeagueEditor: ReducerProtocol {
	public typealias Form = BaseForm<League, Fields>

	public struct Fields: BaseFormState, Equatable {
		public var bowler: Bowler.ID
		@BindableState public var name = ""
		@BindableState public var recurrence: League.Recurrence = .repeating
		@BindableState public var gamesPerSeries: GamesPerSeries = .static
		@BindableState public var numberOfGames = League.DEFAULT_NUMBER_OF_GAMES
		@BindableState public var hasAdditionalPinfall = false
		@BindableState public var additionalPinfall = ""
		@BindableState public var additionalGames = ""
		public var alleyPicker: ResourcePicker<Alley>.State = .init(selected: [], limit: 1)

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty
		}
	}

	public struct State: Equatable {
		public var bowler: Bowler
		public var base: Form.State
		public var initialAlley: Alley?
		public var isAlleyPickerPresented = false
		public let hasAlleysEnabled: Bool

		public init(bowler: Bowler, mode: Form.Mode, hasAlleysEnabled: Bool) {
			self.bowler = bowler
			var fields = Fields(bowler: bowler.id)
			if case let .edit(league) = mode {
				fields.name = league.name
				fields.recurrence = league.recurrence
				fields.numberOfGames = league.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES
				fields.gamesPerSeries = league.numberOfGames == nil ? .dynamic : .static
				fields.additionalGames = "\(league.additionalGames ?? 0)"
				fields.additionalPinfall = "\(league.additionalPinfall ?? 0)"
				fields.hasAdditionalPinfall = (league.additionalGames ?? 0) > 0
			}

			fields.alleyPicker = .init(selected: Set([mode.model?.alley].compactMap({ $0 })), limit: 1)
			self.hasAlleysEnabled = hasAlleysEnabled
			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum GamesPerSeries: Int, Equatable, Identifiable, CaseIterable, CustomStringConvertible {
		case `static`
		case dynamic

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .static: return Strings.League.Editor.Fields.GamesPerSeries.constant
			case .dynamic: return Strings.League.Editor.Fields.GamesPerSeries.alwaysAskMe
			}
		}
	}

	public enum Action: BindableAction, Equatable {
		case loadInitialData
		case leagueAlleyResponse(TaskResult<Alley?>)
		case binding(BindingAction<State>)
		case form(Form.Action)
		case alleyPicker(ResourcePicker<Alley>.Action)
		case setAlleyPickerSheet(isPresented: Bool)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.alleysDataProvider) var alleysDataProvider
	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createLeague,
					update: persistenceService.updateLeague,
					delete: persistenceService.deleteLeague
				))
		}

		Scope(state: \.base.form.alleyPicker, action: /Action.alleyPicker) {
			ResourcePicker { try await alleysDataProvider.fetchAlleys(.init(ordering: .byName)) }
		}

		Reduce { state, action in
			switch action {
			case .loadInitialData:
				if case let .edit(league) = state.base.mode, let alleyId = league.alley {
					return .task {
						await .leagueAlleyResponse(TaskResult {
							let alleys = try await alleysDataProvider.fetchAlleys(.init(filter: [.id(alleyId)], ordering: .byName))
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

			case .binding(\.base.form.$recurrence):
				if state.base.form.recurrence == .oneTimeEvent {
					return .task { .set(\.base.form.$gamesPerSeries, .static) }
				}
				return .none

			case .binding, .form, .alleyPicker:
				return .none
			}
		}
	}
}

extension LeagueEditor.Fields {
	public func model(fromExisting existing: League?) -> League {
		@Dependency(\.uuid) var uuid: UUIDGenerator

		let numberOfGames = gamesPerSeries == .static ? self.numberOfGames : nil
		let additionalGames = hasAdditionalPinfall ? Int(additionalGames) : nil
		let additionalPinfall: Int?
		if let additionalGames {
			additionalPinfall = hasAdditionalPinfall && additionalGames > 0 ? Int(self.additionalPinfall) : nil
		} else {
			additionalPinfall = nil
		}

		return .init(
			bowler: bowler,
			id: existing?.id ?? uuid(),
			name: name,
			recurrence: recurrence,
			numberOfGames: numberOfGames,
			additionalPinfall: additionalPinfall,
			additionalGames: additionalGames,
			alley: alleyPicker.selected.first
		)
	}
}
