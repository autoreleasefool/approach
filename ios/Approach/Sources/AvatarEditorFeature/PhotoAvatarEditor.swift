import AssetsLibrary
import ComposableArchitecture
import EquatablePackageLibrary
import FeatureActionLibrary
import ModelsLibrary
import Photos
import PhotosUI
import StringsLibrary
import SwiftUI

@Reducer
public struct PhotoAvatarEditor: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var imageState: ImageState
		public var photosPickerItem: PhotosPickerItem?

		var value: Avatar.Value? {
			if let data = imageState.photoData {
				.data(data.data)
			} else {
				nil
			}
		}

		public init(avatar: Avatar.Summary?) {
			switch avatar?.value {
			case .text, .url, .none:
				self.imageState = .empty
			case let .data(data):
				self.imageState = if let image = UIImage(data: data) {
					.success(PhotoData(data: data, image: image))
				} else {
					.empty
				}
			}
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View { case doNothing }
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didStartLoadingPhoto
			case didLoadPhoto(Result<PhotoData?, Error>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum ImageState: Equatable {
		case empty
		case loading
		case success(PhotoData)
		case failure(AlwaysEqual<Error>)

		var photoData: PhotoData? {
			switch self {
			case let .success(data): data
			case .empty, .loading, .failure: nil
			}
		}

		var isLoading: Bool {
			switch self {
			case .success, .empty, .failure: false
			case .loading: true
			}
		}
	}

	public struct PhotoData: Equatable {
		public let data: Data
		public let image: UIImage
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer()
			.onChange(of: \.photosPickerItem) { _, item in
				Reduce<State, Action> { _, _ in
					return .run { @MainActor send in
						guard let item else {
							send(.internal(.didLoadPhoto(.success(nil))))
							return
						}

						send(.internal(.didStartLoadingPhoto))
						await send(.internal(.didLoadPhoto(Result {
							try await loadTransferable(from: item)
						})))
					}
				}
			}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .doNothing:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didStartLoadingPhoto:
					state.imageState = .loading
					return .none

				case let .didLoadPhoto(.success(photoData)):
					state.imageState = if let photoData {
						.success(photoData)
					} else {
						.empty
					}
					return .none

				case let .didLoadPhoto(.failure(error)):
					state.imageState = .failure(.init(error))
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
	}

	private func loadTransferable(from imageSelection: PhotosPickerItem) async throws -> PhotoData? {
		let avatarImage = try await imageSelection.loadTransferable(type: AvatarImage.self)
		guard let avatarImage else { return nil }
		return PhotoData(data: avatarImage.data, image: avatarImage.image)
	}
}

@ViewAction(for: PhotoAvatarEditor.self)
public struct PhotoAvatarEditorView: View {
	@Bindable public var store: StoreOf<PhotoAvatarEditor>

	public init(store: StoreOf<PhotoAvatarEditor>) {
		self.store = store
	}

	public var body: some View {
		Section {
			PhotosPicker(
				Strings.Avatar.Editor.Photo.choosePhoto,
				selection: $store.photosPickerItem,
				matching: .images,
				photoLibrary: .shared()
			)
		}
	}
}
