import AlleysRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import FormLibrary
import LeaguesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

public typealias LeagueForm = Form<League.Create, League.Edit>

public struct LeagueEditor: Reducer {
	public struct State: Equatable {
		public let hasAlleysEnabled: Bool

		@BindingState public var name: String
		@BindingState public var recurrence: League.Recurrence
		@BindingState public var numberOfGames: Int?
		@BindingState public var additionalPinfall: Int?
		@BindingState public var additionalGames: Int?
		@BindingState public var excludeFromStatistics: League.ExcludeFromStatistics
		public var location: Alley.Summary?

		@BindingState public var gamesPerSeries: GamesPerSeries
		@BindingState public var hasAdditionalPinfall: Bool

		public let initialValue: LeagueForm.Value
		public var _form: LeagueForm.State

		public var alleyPicker: ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.State
		public var isAlleyPickerPresented = false

		public init(value: LeagueForm.Value) {
			let numberOfGames: Int?
			let additionalGames: Int?
			switch value {
			case let .create(new):
				self.name = new.name
				self.recurrence = new.recurrence
				self.additionalPinfall = new.additionalPinfall
				self.excludeFromStatistics = new.excludeFromStatistics
				self.location = new.location
				numberOfGames = new.numberOfGames
				additionalGames = new.additionalGames
				self.initialValue = .create(new)
			case let .edit(existing):
				self.name = existing.name
				self.recurrence = existing.recurrence
				self.additionalPinfall = existing.additionalPinfall
				self.excludeFromStatistics = existing.excludeFromStatistics
				self.location = existing.location
				numberOfGames = existing.numberOfGames
				additionalGames = existing.additionalGames
				self.initialValue = .edit(existing)
			}
			self._form = .init(initialValue: self.initialValue, currentValue: self.initialValue)
			self.gamesPerSeries = numberOfGames == nil ? .dynamic : .static
			self.hasAdditionalPinfall = (additionalGames ?? 0) > 0

			self.alleyPicker = .init(
				selected: Set([self.location?.id].compactMap { $0 }),
				query: .init(()),
				limit: 1,
				showsCancelHeaderButton: false
			)

			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.hasAlleysEnabled = featureFlags.isEnabled(.alleys)
		}
	}

	public enum GamesPerSeries: Int, Equatable, Identifiable, CaseIterable {
		case `static`
		case dynamic

		public var id: Int { rawValue }
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case setAlleyPicker(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case form(LeagueForm.Action)
			case alleyPicker(ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.leagues) var leagues
	@Dependency(\.uuid) var uuid

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			LeagueForm()
				.dependency(\.records, .init(
					create: leagues.create,
					update: leagues.update,
					delete: leagues.delete
				))
		}

		Scope(state: \.alleyPicker, action: /Action.internal..Action.InternalAction.alleyPicker) {
			ResourcePicker { _ in alleys.list(ordered: .byName) }
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setAlleyPicker(isPresented):
					state.isAlleyPickerPresented = isPresented
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .alleyPicker(.delegate(delegateAction)):
					switch delegateAction {
					case let .didChangeSelection(alley):
						state.location = alley.first
						return .none
					}

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						return state._form.didFinishCreating(result)
							.map { .internal(.form($0)) }

					case let .didUpdate(result):
						return state._form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didDelete(result):
						return state._form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case .didFinishCreating, .didFinishUpdating, .didFinishDeleting, .didDiscard:
						return .run { _ in await self.dismiss() }
					}

				case .form(.internal), .form(.view):
					return .none

				case .alleyPicker(.internal), .alleyPicker(.view):
					return .none
				}

			case .binding(\.view.$recurrence):
				switch state.recurrence {
				case .once:
					state.gamesPerSeries = .static
				case .repeating:
					state.location = nil
				}
				return .none

			case .delegate, .binding:
				return .none
			}
		}
	}
}

extension LeagueEditor.State {
	var form: LeagueForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.name = name
				new.recurrence = recurrence
				new.numberOfGames = gamesPerSeries == .static ? numberOfGames : nil
				new.additionalGames = hasAdditionalPinfall ? additionalGames : nil
				new.additionalPinfall = hasAdditionalPinfall && (additionalGames ?? 0) > 0 ? additionalPinfall : nil
				new.excludeFromStatistics = excludeFromStatistics
				new.location = location
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				existing.additionalGames = hasAdditionalPinfall ? additionalGames : nil
				existing.additionalPinfall = hasAdditionalPinfall && (additionalGames ?? 0) > 0 ? additionalPinfall : nil
				existing.excludeFromStatistics = excludeFromStatistics
				existing.location = location
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}

extension League.Create: CreateableRecord {
	public static var modelName = Strings.League.title

	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension League.Edit: EditableRecord {
	public var isDeleteable: Bool { true }
	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension Alley.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Alley.title : Strings.Alley.List.title
	}
}

extension League.Recurrence: CustomStringConvertible {
	public var description: String {
		switch self {
		case .repeating: return Strings.League.Properties.Recurrence.repeats
		case .once: return Strings.League.Properties.Recurrence.never
		}
	}
}

extension LeagueEditor.GamesPerSeries: CustomStringConvertible {
	public var description: String {
		switch self {
		case .static: return Strings.League.Editor.Fields.GamesPerSeries.constant
		case .dynamic: return Strings.League.Editor.Fields.GamesPerSeries.alwaysAskMe
		}
	}
}
