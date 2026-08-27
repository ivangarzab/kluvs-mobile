//
//  SettingsViewModelWrapper.swift
//  iosApp
//
import Swift
import Shared


@MainActor
class SettingsViewModelWrapper: ObservableObject {
    @Published var isLoading: Bool = true
    @Published var error: String? = nil
    @Published var profile: Shared.EditableProfile? = nil
    @Published var editedName: String = ""
    @Published var editedHandle: String = ""
    @Published var isSaving: Bool = false
    @Published var saveError: String? = nil
    @Published var saveSuccess: Bool = false
    @Published var hasChanges: Bool = false
    @Published var isUploadingAvatar: Bool = false
    @Published var avatarError: String? = nil

    private let helper: SettingsViewModelHelper
    private var cancellables: [Shared.Closeable] = []

    init() {
        self.helper = SettingsViewModelHelper()
        startObserving()
    }

    private func startObserving() {
        let stateCancellable = helper.observeState { [weak self] state in
            Task { @MainActor [weak self] in
                guard let self else { return }
                self.isLoading = state.isLoading
                self.error = state.error
                self.profile = state.profile
                self.editedName = state.editedName
                self.editedHandle = state.editedHandle
                self.isSaving = state.isSaving
                self.saveError = state.saveError
                self.saveSuccess = state.saveSuccess
                self.hasChanges = state.hasChanges
                self.isUploadingAvatar = state.isUploadingAvatar
                self.avatarError = state.avatarError
            }
        }
        cancellables.append(stateCancellable)
    }

    func loadProfile(userId: String) {
        helper.loadProfile(userId: userId)
    }

    func onNameChanged(_ name: String) {
        helper.onNameChanged(name: name)
    }

    func onHandleChanged(_ handle: String) {
        helper.onHandleChanged(handle: handle)
    }

    func onSaveProfile() {
        helper.onSaveProfile()
    }

    func onDismissSaveSuccess() {
        helper.onDismissSaveSuccess()
    }

    func uploadAvatar(imageData: Data) {
        let byteArray = KotlinByteArray(size: Int32(imageData.count))
        imageData.withUnsafeBytes { (bytes: UnsafeRawBufferPointer) in
            if let baseAddress = bytes.baseAddress {
                for i in 0..<imageData.count {
                    byteArray.set(index: Int32(i), value: Int8(bitPattern: baseAddress.load(fromByteOffset: i, as: UInt8.self)))
                }
            }
        }
        helper.uploadAvatar(imageData: byteArray)
    }

    func onAvatarPickFailed(reason: String?) {
        helper.onAvatarPickFailed(reason: reason)
    }

    func clearAvatarError() {
        helper.clearAvatarError()
    }

    deinit {
        cancellables.forEach { $0.close() }
    }
}
