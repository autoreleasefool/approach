import AlleysRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import FormFeature
import LeaguesRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

public typealias LeagueForm = Form<League.Create, League.Edit>

public struct LeagueEditor: Reducer {
	public struct State: Equatable {
		public let hasAlleysEnabled: Bool

		@BindingState public var name: String
		@BindingState public var recurrence: League.Recurrence
		@BindingState public var numberOfGames: Int
		@BindingState public var additionalPinfall: String
		@BindingState public var additionalGames: String
		@BindingState public var excludeFromStatistics: League.ExcludeFromStatistics
		public var location: Alley.Summary?

		@BindingState public var gamesPerSeries: GamesPerSeries
		@BindingState public var hasAdditionalPinfall: Bool
		public var shouldShowLocationSection: Bool

		public let initialValue: LeagueForm.Value
		public var _form: LeagueForm.State

		@PresentationState public var alleyPicker: ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.State?

		public init(value: LeagueForm.Value) {
			let numberOfGames: Int
			let additionalGames: Int
			switch value {
			case let .create(new):
				self.name = new.name
				self.recurrence = new.recurrence
				let additionalPinfall = new.additionalPinfall ?? 0
				self.additionalPinfall = additionalPinfall > 0 ? String(additionalPinfall) : ""
				self.excludeFromStatistics = new.excludeFromStatistics
				self.location = new.location
				numberOfGames = new.numberOfGames ?? 0
				additionalGames = new.additionalGames ?? 0
				self.shouldShowLocationSection = new.recurrence.shouldShowLocationSection
				self.initialValue = .create(new)
			case let .edit(existing):
				self.name = existing.name
				self.recurrence = existing.recurrence
				let additionalPinfall = existing.additionalPinfall ?? 0
				self.additionalPinfall = additionalPinfall > 0 ? String(additionalPinfall) : ""
				self.excludeFromStatistics = existing.excludeFromStatistics
				self.location = existing.location
				numberOfGames = existing.numberOfGames ?? 0
				additionalGames = existing.additionalGames ?? 0
				self.shouldShowLocationSection = existing.recurrence.shouldShowLocationSection
				self.initialValue = .edit(existing)
			}
			self._form = .init(initialValue: self.initialValue, currentValue: self.initialValue)
			self.gamesPerSeries = numberOfGames == 0 ? .dynamic : .static
			self.hasAdditionalPinfall = additionalGames > 0
			self.numberOfGames = numberOfGames
			self.additionalGames = additionalGames > 0 ? String(additionalGames) : ""

			@Dependency(\.featureFlags) var featureFlags
			self.hasAlleysEnabled = featureFlags.isEnabled(.alleys)
		}
	}

	public enum GamesPerSeries: Int, Equatable, Identifiable, CaseIterable {
		case `static`
		case dynamic

		public var id: Int { rawValue }
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didTapAlley
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case setLocationSection(isShown: Bool)
			case form(LeagueForm.Action)
			case alleyPicker(PresentationAction<ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.Action>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.leagues) var leagues
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			LeagueForm()
				.dependency(\.records, .init(
					create: leagues.create,
					update: leagues.update,
					delete: leagues.delete
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapAlley:
					state.alleyPicker = .init(
						selected: Set([state.location?.id].compactMap { $0 }),
						query: .init(()),
						limit: 1,
						showsCancelHeaderButton: false
					)
					return .none

				case .binding(\.$recurrence):
					switch state.recurrence {
					case .once:
						state.gamesPerSeries = .static
						return .run { send in await send(.internal(.setLocationSection(isShown: true)), animation: .easeInOut) }
					case .repeating:
						state.location = nil
						return .run { send in await send(.internal(.setLocationSection(isShown: false)), animation: .easeInOut) }
					}

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .setLocationSection(isShown):
					state.shouldShowLocationSection = isShown
					return .none

				case let .alleyPicker(.presented(.delegate(delegateAction))):
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
						return .run { _ in await dismiss() }
					}

				case .form(.internal), .form(.view):
					return .none

				case .alleyPicker(.presented(.internal)), .alleyPicker(.presented(.view)), .alleyPicker(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$alleyPicker, action: /Action.internal..Action.InternalAction.alleyPicker) {
			ResourcePicker { _ in alleys.list(ordered: .byName) }
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .internal(.form(.delegate(.didFinishCreating))):
				return Analytics.League.Created()
			case .internal(.form(.delegate(.didFinishUpdating))):
				return Analytics.League.Updated()
			case .internal(.form(.delegate(.didFinishDeleting))):
				return Analytics.League.Deleted()
			default:
				return nil
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
				new.numberOfGames = gamesPerSeries == .static ? Int(numberOfGames) : nil
				new.additionalGames = hasAdditionalPinfall ? Int(additionalGames) : nil
				new.additionalPinfall = hasAdditionalPinfall && (new.additionalGames ?? 0) > 0 ? Int(additionalPinfall) : nil
				new.excludeFromStatistics = excludeFromStatistics
				new.location = location
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				existing.additionalGames = hasAdditionalPinfall ? Int(additionalGames) : nil
				existing.additionalPinfall =
					hasAdditionalPinfall && (existing.additionalGames ?? 0) > 0 ? Int(additionalPinfall) : nil
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

extension League.Recurrence: CustomStringConvertible {
	public var description: String {
		switch self {
		case .repeating: return Strings.League.Properties.Recurrence.repeats
		case .once: return Strings.League.Properties.Recurrence.never
		}
	}

	var shouldShowLocationSection: Bool {
		switch self {
		case .repeating:
			return false
		case .once:
			return true
		}
	}
}

extension League.ExcludeFromStatistics: CustomStringConvertible {
	public var description: String {
		switch self {
		case .include: return Strings.League.Properties.ExcludeFromStatistics.include
		case .exclude: return Strings.League.Properties.ExcludeFromStatistics.exclude
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
