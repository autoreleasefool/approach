import AlleysRepositoryInterface
import BaseFormLibrary
import ComposableArchitecture
import EquatableLibrary
import ExtensionsLibrary
import FeatureActionLibrary
import LeaguesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

extension League.Editable: BaseFormModel {
	static public var modelName = Strings.League.title
}

extension League.Recurrence: CustomStringConvertible {
	public var description: String {
		switch self {
		case .repeating: return Strings.League.Properties.Recurrence.repeats
		case .once: return Strings.League.Properties.Recurrence.never
		}
	}
}

extension Alley.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Alley.title : Strings.Alley.List.title
	}
}

public struct LeagueEditor: Reducer {
	public typealias Form = BaseForm<League.Editable, Fields>

	public struct Fields: BaseFormState, Equatable {
		public var alleyPicker: ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.State
		@BindingState public var league: League.Editable
		@BindingState public var gamesPerSeries: GamesPerSeries
		@BindingState public var hasAdditionalPinfall: Bool

		init(league: League.Editable) {
			self.league = league
			self.hasAdditionalPinfall = (league.additionalGames ?? 0) > 0
			self.gamesPerSeries = (league.numberOfGames == nil) ? .dynamic : .static
			self.alleyPicker = .init(
				selected: Set([league.alleyId].compactMap({ $0 })),
				query: .init(()),
				limit: 1,
				showsCancelHeaderButton: false
			)
		}

		public let isDeleteable = true
		public var isSaveable: Bool {
			!league.name.isEmpty
		}
	}

	public struct State: Equatable {
		public var base: Form.State
		public var initialAlley: Alley.Summary?
		public var isAlleyPickerPresented = false
		public let hasAlleysEnabled: Bool

		public init(bowler: Bowler.ID, mode: Form.Mode, hasAlleysEnabled: Bool) {
			var fields: Fields
			switch mode {
			case let .edit(league):
				fields = .init(league: league)
			case .create:
				@Dependency(\.uuid) var uuid: UUIDGenerator
				fields = Fields(league: .init(
					bowlerId: bowler,
					id: uuid(),
					name: "",
					recurrence: .repeating,
					numberOfGames: League.DEFAULT_NUMBER_OF_GAMES,
					additionalPinfall: nil,
					additionalGames: nil,
					excludeFromStatistics: .include,
					alleyId: nil
				))
			}
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

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case setAlleyPicker(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case didLoadAlley(TaskResult<Alley.Summary?>)
			case form(Form.Action)
			case alleyPicker(ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.alleys) var alleys
	@Dependency(\.leagues) var leagues

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.internal..Action.InternalAction.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					save: leagues.save,
					delete: { try await leagues.delete($0.id) }
				))
		}

		Scope(state: \.base.form.alleyPicker, action: /Action.internal..Action.InternalAction.alleyPicker) {
			ResourcePicker { _ in alleys.list(ordered: .byName) }
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					if case let .edit(league) = state.base.mode, let alley = league.alleyId {
						return .run { send in
							for try await alley in alleys.load(alley) {
								await send(.internal(.didLoadAlley(.success(alley))))
							}
						} catch: { error, send in
							await send(.internal(.didLoadAlley(.failure(error))))
						}
					}
					return .none

				case let .setAlleyPicker(isPresented):
					state.isAlleyPickerPresented = isPresented
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

				case let .alleyPicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.isAlleyPickerPresented = false
						return .none
					}

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didSaveModel(league):
						return .task { .internal(.form(.callback(.didFinishSaving(.success(league))))) }

					case let .didDeleteModel(league):
						return .task { .internal(.form(.callback(.didFinishDeleting(.success(league))))) }

					case .didFinishSaving, .didFinishDeleting, .didDiscard:
						return .task { .delegate(.didFinishEditing) }
					}

				case .form(.internal), .form(.view), .form(.callback):
					return .none

				case .alleyPicker(.internal), .alleyPicker(.view):
					return .none
				}

			case .binding(\.base.form.$league.recurrence):
				if state.base.form.league.recurrence == .once {
					state.base.form.gamesPerSeries = .static
				}
				return .none

			case .delegate, .binding:
				return .none
			}
		}
	}
}

extension LeagueEditor.Fields {
	public var model: League.Editable {
		let numberOfGames = gamesPerSeries == .static ? league.numberOfGames : nil
		let additionalGames = hasAdditionalPinfall ? league.additionalGames : nil
		let additionalPinfall: Int?
		if let additionalGames {
			additionalPinfall = hasAdditionalPinfall && additionalGames > 0 ? league.additionalPinfall : nil
		} else {
			additionalPinfall = nil
		}

		return .init(
			bowlerId: league.bowlerId,
			id: league.id,
			name: league.name,
			recurrence: league.recurrence,
			numberOfGames: numberOfGames,
			additionalPinfall: additionalPinfall,
			additionalGames: additionalGames,
			excludeFromStatistics: league.excludeFromStatistics,
			alleyId: alleyPicker.selected.first
		)
	}
}
